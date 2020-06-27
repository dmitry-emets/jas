package com.demets.jas.model

import com.demets.jas.repository.api.db.room.TrackEntity
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by dmitr on 11.02.2018.
 */
data class Track(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val timestamp: Long,
    val id: Long = 0
) {
    fun mapToEntity(scrobbled: Boolean = false, extId: Long? = null): TrackEntity {
        val ts = if (timestamp == 0L) 0 else timestamp / 1000
        return TrackEntity(
                extId ?: id, title, artist, album, duration, scrobbled,
                LocalDateTime.ofInstant
                (Instant.ofEpochSecond(ts), DateTimeUtils.toZoneId(TimeZone.getDefault()))
        )
    }
}