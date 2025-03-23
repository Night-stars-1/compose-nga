package com.srap.nga.logic.network.deserializer

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.srap.nga.logic.model.TopicSubjectResponse.Result.Data.Attach

/**
 * TopicSubject attachs 字段兼容
 */
class AttachListOrStringAdapter : TypeAdapter<List<Attach>>() {
    private val gson = Gson()

    override fun write(out: JsonWriter, value: List<Attach>?) {
        out.beginArray()
        value?.forEach { _ ->
            out.beginObject()
            out.endObject()
        }
        out.endArray()
    }

    override fun read(reader: JsonReader): List<Attach>? {
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                val type = object : TypeToken<List<Attach>>() {}.type
                gson.fromJson(reader, type)
            }
            JsonToken.STRING -> {
                reader.nextString()
                null
            }
            else -> {
                reader.skipValue()
                emptyList()
            }
        }
    }
}