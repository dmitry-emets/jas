package com.demets.jas.db.contract

import android.provider.BaseColumns

/**
 * Created by DEmets on 07.02.2018.
 */
object TrackContract {
    object TrackEntry : BaseColumns {
        const val TABLE_NAME = "tracks_table"
        const val COLUMN_TRACK = "track"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_ALBUM = "album"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_SCROBBLED = "scrobbled"
        const val COLUMN_TIME = "time"

        const val NUM_COL_ID = 0
        const val NUM_COL_TRACK = 1
        const val NUM_COL_ARTIST = 2
        const val NUM_COL_ALBUM = 3
        const val NUM_COL_DURATION = 4
        const val NUM_COL_SCROBBLED = 5
        const val NUM_COL_TIME = 6
    }
}