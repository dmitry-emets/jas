package com.demets.jas.db.room

import androidx.room.*

@Dao
interface TrackDao {
    @Query("SELECT * FROM trackentity")
    fun getAll(): List<TrackEntity>

    @Query("SELECT * FROM trackentity ORDER BY time DESC")
    fun getAllReversed(): List<TrackEntity>

    @Query("SELECT * FROM trackentity WHERE NOT(scrobbled)")
    fun getAllUnscrobbled(): List<TrackEntity>

    @Query("SELECT * FROM trackentity WHERE time>=date('now', 'localtime', 'start of day')")
    fun getScrobbledToday(): List<TrackEntity>

    @Insert
    fun addTracks(track: TrackEntity): Long

    @Update
    fun updateTrack(track: TrackEntity)

    @Delete
    fun delete(track: TrackEntity)
}