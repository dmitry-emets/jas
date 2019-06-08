package com.demets.jas.ui.tracks

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.demets.jas.AppSettings
import com.demets.jas.api.LfApiService
import com.demets.jas.db.TrackDbHelper
import com.demets.jas.db.contract.TrackContract
import com.demets.jas.model.Track
import com.demets.jas.service.JASService
import com.demets.jas.ui.main.authorised.AuthorizedPresenter
import com.demets.jas.utils.TaggedLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.sql.Timestamp

@InjectViewState
class TracksPresenter : MvpPresenter<ITracksView>() {
    private lateinit var context: Context
    private lateinit var mDb: SQLiteDatabase
    private val lfApiService = LfApiService.create()

    fun init(context: Context) {
        this.context = context.applicationContext
        mDb = TrackDbHelper(this.context).writableDatabase
    }

    fun processIntent(intent: Intent) {
        if (intent.action == AuthorizedPresenter.ACTION_TRACK_SCROBBLED) {
            viewState.updateView(getAllTracks())
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.updateView(getAllTracks())
        sendScrobbles()
    }

    private fun getAllTracks() = mDb.query(
            TrackContract.TrackEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            TrackContract.TrackEntry.COLUMN_TIME + " DESC"
    )


    fun sendScrobbles() {
        val cursor = getUnscrobbledTracks()
        cursor.moveToFirst()
        for (i in 0 until cursor.count) {
            val id = cursor.getLong(TrackContract.TrackEntry.NUM_COL_ID)
            val title = cursor.getString(TrackContract.TrackEntry.NUM_COL_TRACK)
            val artist = cursor.getString(TrackContract.TrackEntry.NUM_COL_ARTIST)
            val album = cursor.getString(TrackContract.TrackEntry.NUM_COL_ALBUM)
            val duration = cursor.getLong(TrackContract.TrackEntry.NUM_COL_DURATION)
            val timestamp = Timestamp.valueOf(cursor.getString(TrackContract.TrackEntry.NUM_COL_TIME))
            println("$id $title - $artist $album $duration $timestamp")
            val track = Track(title, artist, album, duration, timestamp.time / 1000, id)
            cursor.moveToNext()
            lfApiService
                    .trackScrobble(JASService.tracksAsScrobbleMap(listOf(track)),
                            AppSettings.getSessionKey(context))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        TaggedLogger.d("Track scrobbled: ${track.artist} - ${track.title}")
                        mDb.update(
                                TrackContract.TrackEntry.TABLE_NAME,
                                TrackDbHelper.trackAsCV(track, true),
                                "_id = ?",
                                arrayOf(id.toString())
                        )
//                        mDb.close()
                        val trackScrobbledIntent = Intent(AuthorizedPresenter.ACTION_TRACK_SCROBBLED)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(trackScrobbledIntent)
                        cursor.moveToNext()
                    }, {
                        TaggedLogger.d("Scrobbling failed: ${track.artist} - ${track.title}")
                        cursor.moveToNext()
                    })
//        }
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

    private fun getUnscrobbledTracks(): Cursor {
        return mDb.query(
                TrackContract.TrackEntry.TABLE_NAME,
                null,
                "${TrackContract.TrackEntry.COLUMN_SCROBBLED}=0",
                null,
                null,
                null,
                TrackContract.TrackEntry.COLUMN_TIME + " DESC"
        )
    }

    companion object {
        const val ACTION_TRACK_SCROBBLED = "ACTION_TRACK_SCROBBLED"
    }
}
