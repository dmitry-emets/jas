package com.demets.jas.model

/**
 * Created by dmitr on 11.02.2018.
 */
data class Track(
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long,
        val timestamp: Long,
        val id: Long = -1
)