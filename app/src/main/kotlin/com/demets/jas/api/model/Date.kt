package com.demets.jas.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 26/11/2017.
 */
data class Date(
        @SerializedName("uts")
        val unixTime: String,
        @SerializedName("#text")
        val text: String
)