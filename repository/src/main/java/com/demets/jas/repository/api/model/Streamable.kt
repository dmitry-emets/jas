package com.demets.jas.repository.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 26/11/2017.
 */
data class Streamable(
        @SerializedName("#text")
        val text: String,
        @SerializedName("fulltrack")
        val fullTrack: String
)
