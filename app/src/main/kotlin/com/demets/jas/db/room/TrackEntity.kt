package com.demets.jas.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val artist: String,
    @ColumnInfo
    val album: String,
    @ColumnInfo
    val duration: Long,
    @ColumnInfo
    val scrobbled: Boolean,
    @ColumnInfo
    val time: LocalDateTime
)