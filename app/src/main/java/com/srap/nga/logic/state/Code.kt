package com.srap.nga.logic.state

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

enum class Code(val value: Int) {
    SUCCESS(0),
    LOGIN_ERROR(1),
    NOT_LOGIN(46),

    CODE_ERROR(-1);

    companion object {
        fun fromValue(value: Int): Code {
            return entries.firstOrNull { it.value == value } ?: CODE_ERROR
        }
    }
}

class CodeAdapter : TypeAdapter<Code>() {
    override fun write(out: JsonWriter, value: Code) {
        out.value(value.value)
    }

    override fun read(reader: JsonReader): Code {
        return Code.fromValue(reader.nextInt())
    }
}
