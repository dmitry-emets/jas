package com.demets.jas.ui.main.authorised

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.widget.Toast
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.demets.jas.AppSettings
import com.demets.jas.R
import com.demets.jas.api.LfApiService
import com.demets.jas.db.TrackDbHelper
import com.demets.jas.db.contract.TrackContract
import com.demets.jas.utils.TaggedLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.util.Date

@InjectViewState
class AuthorizedPresenter : MvpPresenter<IAuthorizedView>() {
    private val lfApiService = LfApiService.create()
    private var canLove = true
    private var cachedLoved = false

    fun getScrobbles(context: Context) {
        val lastCount = AppSettings.getLastFmCount(context)
        val lastUpdateTime = AppSettings.getLastFmCountTime(context)

        val neverUpdated = lastUpdateTime == -1L
        val tooOldData = System.currentTimeMillis() - lastUpdateTime > 1000 * 60 * 10

        if (!neverUpdated) {
            viewState.updateLastFmCountSuccess(Pair(lastCount.toString(), formatTime(lastUpdateTime, context)))
        }
        if (neverUpdated || tooOldData) {
            fetchScrobbleCount(context)
        }
    }

    fun processIntent(intent: Intent, context: Context) {
        when (intent.action) {
            ACTION_TRACK_START -> {
                val scrobblingEnabled = AppSettings.getScrobblingEnabled(context)
                if (scrobblingEnabled) {
                    val title = intent.getStringExtra(TRACK_TITLE)
                    val artist = intent.getStringExtra(TRACK_ARTIST)
                    viewState.updateNowPlaying("$artist - $title")
                    canLove = true
                    viewState.showLikeFab()
                    viewState.toggleLikeFab(canLove)
                    setLoveFabInServerState(title, artist, context)
                }
            }
            ACTION_TRACK_STOP -> {
                val scrobblingEnabled = AppSettings.getScrobblingEnabled(context)
                val nowPlayingText = if (scrobblingEnabled) {
                    context.getString(R.string.ma_now_scrobbling_value_nothing)
                } else {
                    context.getString(R.string.ma_now_scrobbling_value_scrobbling_disabled)
                }
                viewState.updateNowPlaying(nowPlayingText)
                viewState.hideLikeFab()
                cachedLoved = false
            }
            ACTION_TRACK_SCROBBLED -> {
                countTodayScrobbled(context)
            }
        }
    }

    fun countTodayScrobbled(context: Context) {
        val trackDbHelper = TrackDbHelper(context)
        val cursor = trackDbHelper.writableDatabase
                .query(
                        TrackContract.TrackEntry.TABLE_NAME,
                        null,
                        "${TrackContract.TrackEntry.COLUMN_TIME}>=date('now', 'localtime', 'start of day')",
                        null,
                        null,
                        null,
                        null
                )
        val pair = Pair(cursor.count.toString(), formatTime(System.currentTimeMillis(), context))
        cursor.close()
        trackDbHelper.close()
        viewState.updateTodayScrobbled(pair)
    }

    fun initNowPlaying(context: Context) {
        val isNowPlaying = AppSettings.getPreviousTrackInfo(context)?.isPlayingState
        if (isNowPlaying != null && isNowPlaying) {
            val prevTrack = AppSettings.getPreviousTrackInfo(context)?.track
            val track = prevTrack?.title
            val artist = prevTrack?.artist
            if (track != null && artist != null) {
                viewState.updateNowPlaying("$artist - $track")
                viewState.showLikeFab()
                setLoveFabInServerState(track, artist, context)
            }
        }
    }

    private fun setLoveFabInServerState(track: String, artist: String, context: Context) {
        TaggedLogger.d("$canLove, $cachedLoved")
        if (!cachedLoved) {
            lfApiService.getTrackInfo(track = track, artist = artist, user = AppSettings.getUsername(context))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.result.userloved }
                    .subscribe({
                        cachedLoved = true
                        canLove = it != 1
                        TaggedLogger.d("Can love: $canLove")
                        viewState.toggleLikeFab(canLove)
                    }, {
                        canLove = true
                        viewState.toggleLikeFab(canLove)
                    })
        } else {
            viewState.toggleLikeFab(canLove)
        }
    }

    fun likePressed(context: Context) {
        val track = AppSettings.getPreviousTrackInfo(context)?.track?.title
        val artist = AppSettings.getPreviousTrackInfo(context)?.track?.artist
        val sk = AppSettings.getSessionKey(context)
        if (track != null && artist != null) {
            if (canLove) {
                lfApiService.trackLove(track, artist, sk)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Toast.makeText(context, context.getString(R.string.ma_track_loved_message), Toast.LENGTH_SHORT).show()
                            canLove = false
                            viewState.toggleLikeFab(canLove)
                        }, {
                            Toast.makeText(context, context.getString(R.string.ma_track_love_failed_message), Toast.LENGTH_SHORT).show()
                        })
            } else {
                lfApiService.trackUnlove(track, artist, sk)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Toast.makeText(context, context.getString(R.string.ma_track_unloved_message), Toast.LENGTH_SHORT).show()
                            canLove = true
                            viewState.toggleLikeFab(canLove)
                        }, {
                            Toast.makeText(context, context.getString(R.string.ma_track_unlove_failed_message), Toast.LENGTH_SHORT).show()
                        })
            }
        }

        canLove = !canLove
        viewState.toggleLikeFab(canLove)
    }

    fun fetchScrobbleCount(context: Context) {
        val user = AppSettings.getUsername(context)
        viewState.showRefresher()
        lfApiService.getUserInfo(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.result.playcount }
                .subscribe({
                    val time = System.currentTimeMillis()
                    AppSettings.setLastFmCount(context, it)
                    AppSettings.setLastFmCountTime(context, time)
                    viewState.hideRefresher()
                    viewState.updateLastFmCountSuccess(Pair(it.toString(), formatTime(time, context)))
                }, {
                    val count = AppSettings.getLastFmCount(context)
                    val time = AppSettings.getLastFmCountTime(context)
                    viewState.hideRefresher()
                    viewState.updateLastFmCountFailed(Pair(count.toString(), formatTime(time, context)))
                })
    }

    private fun formatTime(time: Long, context: Context): String {
        //TODO: use resources for strings
        if (time == -1L) {
            return context.getString(R.string.ma_update_time_never)
        }
        if (System.currentTimeMillis() - time < 1000 * 60 * 3) {
            return context.getString(R.string.ma_update_time_just_now)
        }
        val dfDate = DateFormat.getDateInstance(DateFormat.MEDIUM)
        val dfTime = DateFormat.getTimeInstance(DateFormat.SHORT)
        val date = Date(time)
        return if (DateUtils.isToday(time)) {
            String.format(context.getString(R.string.ma_update_time_today), dfTime.format(date))
        } else {
            String.format(context.getString(R.string.ma_update_time_not_today), dfDate.format(date), dfTime.format(date))
        }
    }

    companion object {
        const val ACTION_TRACK_SCROBBLED = "ACTION_TRACK_SCROBBLED"
        const val ACTION_TRACK_START = "ACTION_TRACK_START"
        const val ACTION_TRACK_STOP = "ACTION_TRACK_STOP"
        const val TRACK_TITLE = "title"
        const val TRACK_ARTIST = "artist"
        const val TRACK_ALBUM = "album"
    }
}
