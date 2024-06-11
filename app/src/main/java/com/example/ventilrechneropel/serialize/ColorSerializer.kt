package com.example.ventilrechneropel.serialize

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure


object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Color") {
        element<Float>("red")
        element<Float>("green")
        element<Float>("blue")
        element<Float>("alpha")
    }

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeStructure(descriptor) {
            encodeFloatElement(descriptor, 0, value.red)
            encodeFloatElement(descriptor, 1, value.green)
            encodeFloatElement(descriptor, 2, value.blue)
            encodeFloatElement(descriptor, 3, value.alpha)
        }
    }

    override fun deserialize(decoder: Decoder): Color {
        return decoder.decodeStructure(descriptor) {
            var red = 0f
            var green = 0f
            var blue = 0f
            var alpha = 0f

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> red = decodeFloatElement(descriptor, 0)
                    1 -> green = decodeFloatElement(descriptor, 1)
                    2 -> blue = decodeFloatElement(descriptor, 2)
                    3 -> alpha = decodeFloatElement(descriptor, 3)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw SerializationException("Unknown index $index")
                }
            }
            Color(red, green, blue, alpha)
        }
    }
}