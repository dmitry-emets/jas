package com.demets.jas.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 17/12/2017.
 */
data class Tag(
        val name: String,
        val total: Int,
        val reach: Int,
        val wiki: Wiki
)

data class TagListWrapper(
        @SerializedName("tag")
        val tags: List<Tag>)