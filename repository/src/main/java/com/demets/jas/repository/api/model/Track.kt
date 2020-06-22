package com.demets.jas.repository.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 27/11/2017.
 */
data class Track(
        val name: String,
        val mbid: String,
        val url: String,
        val date: Date,
        val duration: Long,
        val streamable: Streamable,
        val listeners: Int,
        val playcount: Int,
        val artist: Artist,
        val album: Album,
        val userplaycount: Int,
        val userloved: Int,
        val toptags: TagListWrapper,
        val wiki: Wiki,
        val image: Image
)

data class TrackListWrapper(
        @SerializedName("track")
        val tracks: List<Track>
)
