package com.srap.nga.logic.network.deserializer

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.srap.nga.logic.model.QRCodeLoginResponse

/**
 * QRCodeLogin Result字段兼容
 */
class QRCodeLoginResultAdapter : TypeAdapter<QRCodeLoginResponse.Result>() {
    private val gson = Gson()

    override fun write(out: JsonWriter, value: QRCodeLoginResponse.Result?) {
        out.beginArray()
        out.endArray()
    }

    override fun read(reader: JsonReader): QRCodeLoginResponse.Result? {
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                var lastItem: QRCodeLoginResponse.Result? = null

                reader.beginArray()
                while (reader.hasNext()) {
                    if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                        // 每次解析一个对象并覆盖前一个，最终保留最后一个对象
                        lastItem = gson.fromJson(reader, QRCodeLoginResponse.Result::class.java)
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