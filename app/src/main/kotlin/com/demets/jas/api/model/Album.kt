package com.demets.jas.api.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

/**
 * Created by dmitry on 17/12/2017.
 */
data class Album(
        val name: String,
        @JsonAdapter(Deserializer::class)
        val artist: Artist,
        val mbid: String,
        val url: String,
        val image: List<Image>,
        val listeners: Int,
        val playcount: Int,
        val tracks: TrackListWrapper,
        val tags: TagListWrapper,
        val wiki: Wiki
)

data class AlbumListWrapper(
        @SerializedName("album")
        val albums: List<Album>
)

private class Deserializer : JsonDeserializer<Artist> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Artist {
        if (json.isJsonPrimitive) {
            return Artist(json.asString, null, null, null, null, null, null, null, null, null)
        }
        return context.deserialize(json, typeOfT)
    }
}