package com.srap.nga.logic.network.deserializer

import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.srap.nga.logic.model.PostVote
import com.srap.nga.logic.model.PostVoteResponse

class PostVoteResultAdapter : TypeAdapter<PostVoteResponse.Result>() {
    override fun write(out: JsonWriter, value: PostVoteResponse.Result?) {
        if (value == null) {
            out.nullValue()
            return
        }

        out.beginObject()
        out.name("last_state").value(value.lastState)
        out.endObject()
    }

    override fun read(reader: JsonReader): PostVoteResponse.Result? = when (reader.peek()) {
        JsonToken.BEGIN_OBJECT -> readResultObject(reader)
        JsonToken.BEGIN_ARRAY -> readResultArray(reader)
        JsonToken.NULL -> {
            reader.nextNull()
            null
        }
        else -> {
            reader.skipValue()
            null
        }
    }

    private fun readResultArray(reader: JsonReader): PostVoteResponse.Result? {
        var result: PostVoteResponse.Result? = null
        reader.beginArray()
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                val candidate = readResultObject(reader)
                if (result == null) result = candidate
            } else {
                reader.skipValue()
            }
        }
        reader.endArray()
        return result
    }

    private fun readResultObject(reader: JsonReader): PostVoteResponse.Result? {
        val json = JsonParser.parseReader(reader).asJsonObject
        val lastState = runCatching { json.get("last_state")?.asInt }.getOrNull()
            ?.takeIf {
                it == PostVote.DISLIKE || it == PostVote.NONE || it == PostVote.LIKE
            }
            ?: return null
        return PostVoteResponse.Result(lastState = lastState)
    }
}
