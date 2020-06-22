package com.demets.jas.ui.tracks

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.demets.jas.AppSettings
import com.demets.jas.db.TracksRepository
import com.demets.jas.model.Track
import com.demets.jas.repository.api.LfApiService
import com.demets.jas.service.JASService
import com.demets.jas.ui.main.authorised.AuthorizedPresenter
import com.demets.jas.utils.TaggedLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.ZoneId

@InjectViewState
class TracksPresenter : MvpPresenter<ITracksView>() {
    private lateinit var context: Context
    private lateinit var tracksRepository: TracksRepository
    private val lfApiService = LfApiService.create()

    fun init(context: Context) {
        this.context = context.applicationContext
        tracksRepository = TracksRepository(context)
    }

    fun processIntent(intent: Intent) {
        if (intent.action == AuthorizedPresenter.ACTION_TRACK_SCROBBLED) {
            tracksRepository.getScrobbledDesc()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tracks ->
                    viewState.updateView(tracks)
                }
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        tracksRepository.getScrobbledDesc()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tracks -> viewState.updateView(tracks) }
        sendScrobbles()
    }

    private fun sendScrobbles() {
        tracksRepository.getUnscrobbled()
            .subscribe { tracks ->
                tracks.forEach { trackEntity ->
                    println("${trackEntity.uid} ${trackEntity.title} - ${trackEntity.artist} ${trackEntity.album} ${trackEntity.duration} ${trackEntity.time}")
                    val track = Track(
                        trackEntity.title,
                        trackEntity.artist,
                        trackEntity.album,
                        trackEntity.duration,
                        trackEntity.time.atZone(ZoneId.systemDefault()).toEpochSecond(),
                        trackEntity.uid
                    )
                    lfApiService
                        .trackScrobble(
                            JASService.tracksAsScrobbleMap(listOf(track)),
                            AppSettings.getSessionKey(context)
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                                       TaggedLogger.d("Track scrobbled: ${track.artist} - ${track.title}")
                                       val scrobbledTrackEntity = trackEntity.copy(scrobbled = true)
                                       tracksRepository.updateTrack(scrobbledTrackEntity)
                                       val trackScrobbledIntent =
                                           Intent(AuthorizedPresenter.ACTION_TRACK_SCROBBLED)
                                       LocalBroadcastManager.getInstance(context)
                                           .sendBroadcast(trackScrobbledIntent)
                                   }, {
                                       TaggedLogger.d("Scrobbling failed: ${track.artist} - ${track.title}")
                                   })
                }
            }

        //        Observable.zip(Observable.fromIterable(tracks)
        //                //.timeInterval()
        //                , Observable.interval(1, TimeUnit.SECONDS),
        //                BiFunction<Track, Long, Track> {
        //                    track, _ -> track
        //                }
        //        ).map {
        //            val track = it
        //            lfApiService
        //                    .trackScrobble(JASService.tracksAsScrobbleMap(listOf(it)),
        //                            AppSettings.getSessionKey(context))
        //                    .subscribeOn(Schedulers.io())
        //                    .observeOn(AndroidSchedulers.mainThread())
        //                    .subscribe({
        //                        TaggedLogger.d("Track scrobbled: ${track.artist} - ${track.title}")
        //                        mDb.update(
        //                                TrackContract.TrackEntry.TABLE_NAME,
        //                                TrackDbHelper.trackAsCV(track, true),
        //                                "_id = ?",
        //                                arrayOf(track.id.toString())
        //                        )
        ////                        mDb.close()
        //                        val trackScrobbledIntent = Intent(AuthorizedPresenter.ACTION_TRACK_SCROBBLED)
        //                        LocalBroadcastManager.getInstance(context).sendBroadcast(trackScrobbledIntent)
        //                        cursor.moveToNext()
        //                    }, {
        //                        TaggedLogger.d("Scrobbling failed: ${track.artist} - ${track.title}")
        //                        cursor.moveToNext()
        //                    })
        //        }

        //        mRecyclerView.adapter = TrackAdapter(getAllTracks())
        //        mRecyclerView.adapter.notifyDataSetChanged()
    }

    companion object {
        const val ACTION_TRACK_SCROBBLED = "ACTION_TRACK_SCROBBLED"
    }
}
