package com.demets.jas.repository.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 27/11/2017.
 */
data class Artist(
        val name: String,
        val mbid: String?,
        val url: String?,
        val image: List<Image>?,
        val streamable: Int?,
        val ontour: Int?,
        val stats: Stats?,
        val similar: ArtistListWrapper?,
        val tags: TagListWrapper?,
        val bio: Wiki?
)

data class Stats(
        val listeners: Int,
        val playcount: Int
)

data class ArtistListWrapper(
        @SerializedName("artist")
        val artists: List<Artist>
)
