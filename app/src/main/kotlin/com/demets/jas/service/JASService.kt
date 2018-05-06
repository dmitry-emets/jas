package com.demets.jas.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.demets.jas.AppSettings
import com.demets.jas.api.LfApiService
import com.demets.jas.db.TrackDbHelper
import com.demets.jas.db.contract.TrackContract
import com.demets.jas.model.PreviousTrackInfo
import com.demets.jas.model.Track
import com.demets.jas.mvp.presenter.AuthorizedPresenter
import com.demets.jas.receivers.MyBroadcastReceiver
import com.demets.jas.utils.NotificationUtil
import com.demets.jas.utils.TaggedLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by dmitr on 11.02.2018.
 */
class JASService : Service() {

    private lateinit var trackDbHelper: TrackDbHelper
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return if (intent == null || intent.action == null) {
            TaggedLogger.d("Empty or null intent. Intent ignored.")
            START_STICKY
        } else {
            logIntent(intent)

            val trackPlaying = intent.getBooleanExtra(MyBroadcastReceiver.EXTRA_IS_PLAYING, false)
            val trackFromIntent = Track(intent.getStringExtra(MyBroadcastReceiver.EXTRA_TRACK),
                    intent.getStringExtra(MyBroadcastReceiver.EXTRA_ARTIST),
                    intent.getStringExtra(MyBroadcastReceiver.EXTRA_ALBUM),
                    intent.getLongExtra(MyBroadcastReceiver.EXTRA_DURATION, -1L),
                    intent.getLongExtra(MyBroadcastReceiver.EXTRA_TIMESTAMP, -1L))

            //Send intent for UI update
            val intentForUi = if (trackPlaying) {
                Intent(AuthorizedPresenter.ACTION_TRACK_START)
                        .apply {
                            putExtra(AuthorizedPresenter.TRACK_TITLE, trackFromIntent.title)
                            putExtra(AuthorizedPresenter.TRACK_ARTIST, trackFromIntent.artist)
                            putExtra(AuthorizedPresenter.TRACK_ALBUM, trackFromIntent.album)
                        }
            } else {
                Intent(AuthorizedPresenter.ACTION_TRACK_STOP)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentForUi)

            //Update last.fm now playing if track is playing.
            if (trackPlaying) {
                sendNowPlaying(trackFromIntent)
            }

            val previousTrackInfo = AppSettings.getPreviousTrackInfo(this)
            val prevTrackPlaying = previousTrackInfo?.isPlayingState ?: false
            val prevTrack = previousTrackInfo?.track

            //Previous track state was "playing" and now it changed to another state.
            val playingStopped = prevTrackPlaying && !trackPlaying
            val isTrackChanged = (prevTrack?.artist != trackFromIntent.artist ||
                    prevTrack.title != trackFromIntent.title) && prevTrackPlaying

            if (previousTrackInfo != null && (playingStopped || isTrackChanged)) {
                analyzeForScrobble(previousTrackInfo.track)
            }
            AppSettings.setPreviousTrackInfo(this, PreviousTrackInfo(trackFromIntent, trackPlaying))

            START_STICKY
        }
    }

    private fun analyzeForScrobble(prevTrack: Track) {
        NotificationUtil.dismissNowPlaying(this)

        val minDuration = AppSettings.getMinDurationToScrobble(this)
        val minTime = AppSettings.getMinTimeToScrobble(this)
        val minPercent = AppSettings.getMinPercentToScrobble(this)

        val playedTimeInSecs = (System.currentTimeMillis() - prevTrack.timestamp) / 1000
        val playedPercent = playedTimeInSecs * 1000 * 100 / prevTrack.duration

        TaggedLogger.d("Track duration: ${prevTrack.duration}")
        TaggedLogger.d("Min duration: ${minDuration * 1000}")
        TaggedLogger.d("Played time: $playedTimeInSecs")
        TaggedLogger.d("Min time: $minTime")
        TaggedLogger.d("Played percent: $playedPercent")
        TaggedLogger.d("Min percent: $minPercent")

        val canBeScrobbled = AppSettings.isAuthorized(this)
                && AppSettings.getScrobblingEnabled(this)
                && prevTrack.duration > minDuration * 1000
                && playedTimeInSecs > minTime
                && playedPercent > minPercent

        if (canBeScrobbled) {
            if (prevTrack.artist.isNotEmpty() && prevTrack.title.isNotEmpty()) {
                val trackDbId = saveTrackToDb(prevTrack)
                sendScrobbleToLfm(prevTrack, trackDbId)
                triggerToast(prevTrack)
            } else {
                TaggedLogger.d("Empty artist or track. Track ignored.")
            }
        }
    }

    private fun triggerToast(prevTrack: Track) {
        if (AppSettings.getEnableToastOnScrobble(this)) {
            Toast.makeText(this, "Scrobbled: ${prevTrack.artist} - ${prevTrack.title}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendScrobbleToLfm(track: Track, dbId: Long) {
        LfApiService.create()
                .trackScrobble(tracksAsScrobbleMap(listOf(track)),
                        AppSettings.getSessionKey(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    TaggedLogger.d("Track scrobbled: ${track.artist} - ${track.title}")
                    updateDbOnScrobble(track, dbId)
                    val trackScrobbledIntent = Intent(AuthorizedPresenter.ACTION_TRACK_SCROBBLED)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(trackScrobbledIntent)
                }, {
                    TaggedLogger.d("Scrobbling failed: ${track.artist} - ${track.title}")
                })
    }

    private fun updateDbOnScrobble(track: Track, dbId: Long) {
        trackDbHelper.writableDatabase
                .update(
                        TrackContract.TrackEntry.TABLE_NAME,
                        TrackDbHelper.trackAsCV(track, true),
                        "_id = ?",
                        arrayOf(dbId.toString())
                )
        trackDbHelper.close()
    }

    private fun saveTrackToDb(track: Track): Long {
        trackDbHelper = TrackDbHelper(this)
        val id = trackDbHelper
                .writableDatabase
                .insert(
                        TrackContract.TrackEntry.TABLE_NAME,
                        null,
                        TrackDbHelper.trackAsCV(track)
                )
        trackDbHelper.close()
        return id
    }

    private fun sendNowPlaying(track: Track) {
        val isAuthorized = AppSettings.isAuthorized(this)
        if (isAuthorized && track.artist.isNotEmpty() && track.title.isNotEmpty()) {
            NotificationUtil.setNowPlaying(this, track)
            LfApiService.create()
                    .trackUpdateNowPlaying(trackAsNowPlayingMap(track),
                            AppSettings.getSessionKey(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        TaggedLogger.d("Last.fm now playing updated: ${track.artist} - ${track.title}")
                    }, {
                        TaggedLogger.d("Error while updating now playing: ${track.artist} - ${track.title}")
                    })
        }
    }

    private fun logIntent(intent: Intent) {
        TaggedLogger.d("Processing intent: " +
                intent.getStringExtra(MyBroadcastReceiver.EXTRA_ARTIST) + " - " +
                intent.getStringExtra(MyBroadcastReceiver.EXTRA_TRACK) + " (" +
                intent.getBooleanExtra(MyBroadcastReceiver.EXTRA_IS_PLAYING, false) + ")")
    }

    companion object {
        const val ACTION_PROCESS_TRACK = "ACTION_PROCESS_TRACK"

        fun tracksAsScrobbleMap(trackList: List<Track>): Map<String, String> {
            val resultMap = HashMap<String, String>()
            trackList.filterNot { it.artist.isEmpty() || it.title.isEmpty() }
                    .forEachIndexed { index, track ->
                        run {
                            resultMap.apply {
                                put("track[$index]", track.title)
                                put("artist[$index]", track.artist)
                                put("timestamp[$index]", (track.timestamp / 1000).toString())
                                put("duration[$index]", (track.duration / 1000).toString())
                                if (!track.album.isEmpty()) put("album[$index]", track.album)
                                put("chosenByUser[$index]", "1")
                            }
                        }
                    }
            return resultMap
        }

        fun trackAsNowPlayingMap(track: Track): Map<String, String> {
            val resultMap = HashMap<String, String>()
            resultMap.apply {
                put("track", track.title)
                put("artist", track.artist)
                put("duration", (track.duration / 1000).toString())
                if (!track.album.isEmpty()) put("album", track.album)
            }
            return resultMap
        }
    }
}