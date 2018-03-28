package com.demets.jas.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.demets.jas.db.contract.TrackContract
import com.demets.jas.model.Track

/**
 * Created by DEmets on 07.02.2018.
 */
class TrackDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_DATABASE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //describe migration if necessary
    }

    companion object {
        const val DATABASE_NAME = "tracks.db"
        const val DATABASE_VERSION = 1

        const val SQL_CREATE_DATABASE = "CREATE TABLE ${TrackContract.TrackEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${TrackContract.TrackEntry.COLUMN_TRACK} TEXT NOT NULL," +
                "${TrackContract.TrackEntry.COLUMN_ARTIST} TEXT NOT NULL," +
                "${TrackContract.TrackEntry.COLUMN_ALBUM} TEXT NOT NULL," +
                "${TrackContract.TrackEntry.COLUMN_DURATION} LONG NOT NULL," +
                "${TrackContract.TrackEntry.COLUMN_SCROBBLED} INTEGER DEFAULT NULL," +
                "${TrackContract.TrackEntry.COLUMN_TIME} TIMESTAMP DEFAULT (datetime('now','localtime')))"

        const val SQL_DROP_DATABASE = "DROP TABLE IF EXISTS ${TrackContract.TrackEntry.TABLE_NAME}"

        fun trackAsCV(track: Track, scrobbled: Boolean = false): ContentValues = ContentValues().apply {
            put(TrackContract.TrackEntry.COLUMN_TRACK, track.title)
            put(TrackContract.TrackEntry.COLUMN_ARTIST, track.artist)
            put(TrackContract.TrackEntry.COLUMN_ALBUM, track.album)
            put(TrackContract.TrackEntry.COLUMN_DURATION, track.duration)
            put(TrackContract.TrackEntry.COLUMN_SCROBBLED, if (scrobbled) 1 else 0)
        }
    }
}