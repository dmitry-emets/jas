package com.demets.jas.db

import android.content.Context
import com.demets.jas.db.room.AppDatabase
import com.demets.jas.db.room.TrackDao
import com.demets.jas.db.room.TrackEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

//TODO: сделать синглтон
class TracksRepository(private val context: Context) {

    private fun getTrackDao(): Single<TrackDao> {
        return Single.just(AppDatabase.getInstance(context.applicationContext))
            .subscribeOn(Schedulers.io())
            .map { it.trackDao() }
    }

    fun getScrobbled(): Single<List<TrackEntity>> = getTrackDao().map { it.getAll() }

    fun getScrobbledDesc(): Single<List<TrackEntity>> = getTrackDao().map { it.getAllReversed() }

    fun getUnscrobbled(): Single<List<TrackEntity>> = getTrackDao().map { it.getAllUnscrobbled() }

    fun getScrobbledToday(): Single<List<TrackEntity>> =
        getTrackDao().map { it.getScrobbledToday() }

    fun insertTrack(trackEntity: TrackEntity): Single<Long> {
        return getTrackDao().map { it.addTracks(trackEntity) }
    }

    fun updateTrack(trackEntity: TrackEntity) {
        getTrackDao().subscribe { trackDao -> trackDao.updateTrack(trackEntity) }
    }
}