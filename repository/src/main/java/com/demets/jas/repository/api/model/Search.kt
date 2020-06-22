package com.demets.jas.repository.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by DEmets on 18.01.2018.
 */
data class Search<T>(
        @SerializedName("opensearch\\:Query")
        val query: String, //TODO
        @SerializedName("opensearch\\:totalResults")
        val totalResults: Int,
        @SerializedName("opensearch\\:startIndex")
        val startIndex: Int,
        @SerializedName("opensearch\\:itemsPerPage")
        val itemsPerPage: Int,
        @SerializedName("albummatches")
        val searchResult: Wrappable<T> //TODO: fix deserealization
)
