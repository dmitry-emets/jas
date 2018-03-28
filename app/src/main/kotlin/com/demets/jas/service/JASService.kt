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

    lateinit var trackDbHelper: TrackDbHelper
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return if (intent == null || intent.action == null) {
            TaggedLogger.d("Empty or null intent. Intent ignored.")
            START_STICKY
        } else {
            TaggedLogger.d("Processing intent: " +
                    intent.getStringExtra(MyBroadcastReceiver.EXTRA_ARTIST) + " - " +
                    intent.getStringExtra(MyBroadcastReceiver.EXTRA_TRACK) + " (" +
                    intent.getBooleanExtra(MyBroadcastReceiver.EXTRA_IS_PLAYING, false) + ")")

            val isPlaying = intent.getBooleanExtra(MyBroadcastReceiver.EXTRA_IS_PLAYING, false)
            val track = Track(intent.getStringExtra(MyBroadcastReceiver.EXTRA_TRACK),
                    intent.getStringExtra(MyBroadcastReceiver.EXTRA_ARTIST),
                    intent.getStringExtra(MyBroadcastReceiver.EXTRA_ALBUM),
                    intent.getLongExtra(MyBroadcastReceiver.EXTRA_DURATION, -1L),
                    intent.getLongExtra(MyBroadcastReceiver.EXTRA_TIMESTAMP, -1L))

            //Send intent for UI update
            val trackIntent = if (isPlaying) {
                Intent(AuthorizedPresenter.ACTION_TRACK_START)
                        .apply {
                            putExtra(AuthorizedPresenter.TRACK_TITLE, track.title)
                            putExtra(AuthorizedPresenter.TRACK_ARTIST, track.artist)
                            putExtra(AuthorizedPresenter.TRACK_ALBUM, track.album)
                        }
            } else {
                Intent(AuthorizedPresenter.ACTION_TRACK_STOP)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(trackIntent)

            val previousTrackInfo = AppSettings.getPreviousTrackInfo(this)
            //Track is playing.
            if (isPlaying
                    && track.artist.isNotEmpty()
                    && track.title.isNotEmpty()) {
                NotificationUtil.setNowPlaying(this, track)
                LfApiService.create()
                        .trackUpdateNowPlaying(trackAsNowPlayingMap(track),
                                AppSettings.getSessionKey(this))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            TaggedLogger.d("Now playing updated: ${track.artist} - ${track.title}")
                        }, {
                            TaggedLogger.d("Error while updating now playing: ${track.artist} - ${track.title}")
                        })
            }
            val statusWasPlaying = previousTrackInfo?.isPlayingState ?: false
            //Previous track state was "playing" and now it changed to another state.
            val isStateChangedFromPlaying = statusWasPlaying && !isPlaying
            val isTrackChanged = (previousTrackInfo?.track?.artist != track.artist ||
                    previousTrackInfo.track.title != track.title) && statusWasPlaying
            if (previousTrackInfo != null && (isStateChangedFromPlaying || isTrackChanged)) {
                NotificationUtil.dismissNowPlaying(this)
                val prevTrack = previousTrackInfo.track
                val minDuration = AppSettings.getMinDurationToScrobble(this)
                val minTimeToScrobble = AppSettings.getMinTimeToScrobble(this)
                val minPercentToScrobble = AppSettings.getMinPercentToScrobble(this)

                val playedTimeInSecs = (System.currentTimeMillis() - prevTrack.timestamp) / 1000
                val playedPercent = playedTimeInSecs * 1000 * 100 / prevTrack.duration

                TaggedLogger.d("Track duration: ${prevTrack.duration}")
                TaggedLogger.d("Min duration: ${minDuration * 1000}")
                TaggedLogger.d("Played time: $playedTimeInSecs")
                TaggedLogger.d("Min time: $minTimeToScrobble")
                TaggedLogger.d("Played percent: $playedPercent")
                TaggedLogger.d("Min percent: $minPercentToScrobble")

                if (AppSettings.isAuthorized(this)
                        && AppSettings.getScrobblingEnabled(this)
                        && prevTrack.duration > minDuration * 1000
                        && playedTimeInSecs > minTimeToScrobble
                        && playedPercent > minPercentToScrobble) {
                    if (prevTrack.artist.isNotEmpty() && prevTrack.title.isNotEmpty()) {
                        trackDbHelper = TrackDbHelper(this)
                        val trackId = trackDbHelper
                                .writableDatabase
                                .insert(
                                        TrackContract.TrackEntry.TABLE_NAME,
                                        null,
                                        TrackDbHelper.trackAsCV(prevTrack)
                                )
                        trackDbHelper.close()
                        LfApiService.create()
                                .trackScrobble(tracksAsScrobbleMap(listOf(prevTrack)),
                                        AppSettings.getSessionKey(this))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    TaggedLogger.d("Track scrobbled: ${prevTrack.artist} - ${prevTrack.title}")
                                    trackDbHelper.writableDatabase
                                            .update(
                                                    TrackContract.TrackEntry.TABLE_NAME,
                                                    TrackDbHelper.trackAsCV(prevTrack, true),
                                                    "_id = ?",
                                                    arrayOf(trackId.toString())
                                            )
                                    trackDbHelper.close()
                                    val trackScrobbledIntent = Intent(AuthorizedPresenter.ACTION_TRACK_SCROBBLED)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(trackScrobbledIntent)
                                }, {
                                    TaggedLogger.d("Scrobbling failed: ${prevTrack.artist} - ${prevTrack.title}")
                                })
                        if (AppSettings.getEnableToastOnScrobble(this)) {
                            Toast.makeText(this, "Scrobbled: ${prevTrack.artist} - ${prevTrack.title}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        TaggedLogger.d("Empty artist or track. Track ignored.")
                    }
                }
            }
            AppSettings.setPreviousTrackInfo(this, PreviousTrackInfo(track, isPlaying))

            START_STICKY
        }

    }

    companion object {
        const val ACTION_PROCESS_TRACK = "ACTION_PROCESS_TRACK"

        private fun isThisTrack(prevTrack: Track, track: Track) =
                prevTrack.artist == track.artist && prevTrack.title == track.title

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

        fun isSameTrack(prevTrack: Track?, track: Track): Boolean {
            return if (prevTrack == null) {
                false
            } else {
                prevTrack.artist == track.artist && prevTrack.title == track.title
            }
        }
    }
}