package com.demets.jas.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 27/12/2017.
 */
data class Response<T>(
        @SerializedName(value = "user", alternate = ["friends",
            "album",
            "artist", "similarartists", "artists",
            "tags", "toptags",
            "toptracks", "tracks", "track",
            "results",
            "token", "session"])
        val result: T,
        val message: String?, //TODO
        val error: Int? //TODO
)