package com.srap.nga.logic.network.deserializer

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.srap.nga.logic.model.FavoriteResponse

class FavoriteResultAdapter : TypeAdapter<List<FavoriteResponse.Data>>() {
    private val gson = Gson()

    override fun write(out: JsonWriter, value: List<FavoriteResponse.Data>?) {
        out.beginArray()
        out.endArray()
    }

    override fun read(reader: JsonReader): List<FavoriteResponse.Data>? {
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                var lastItem: List<FavoriteResponse.Data>? = null

                reader.beginArray()
                while (reader.hasNext()) {
                    if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                        val type = object : TypeToken<List<FavoriteResponse.Data>>() {}.type
                        lastItem = gson.fromJson(reader, type)
                    } else {
                        // 跳过非对象的元素
                        reader.skipValue()
                    }
                }
                reader.endArray()

                lastItem
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }
}