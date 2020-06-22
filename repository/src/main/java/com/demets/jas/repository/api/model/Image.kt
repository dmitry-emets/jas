package com.demets.jas.repository.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 26/11/2017.
 */
data class Image(
        @SerializedName("#text")
        val text: String,
        val size: String
)
