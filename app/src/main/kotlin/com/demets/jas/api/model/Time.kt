package com.demets.jas.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 26/11/2017.
 */
data class Time(
        @SerializedName("#text")
        val text: String,
        @SerializedName("unixtime")
        val unixTime: String
)