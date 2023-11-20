package org.dynodict.plugin.generation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import okhttp3.internal.toHexString
import org.junit.Test

//@Serializable(with = TranslationSerializer::class)
//sealed class Translation {
//
//    data class Value(val value: String, val params: List<RemoteParameter> = emptyList()) : Translation()
//    data class Container(val children: List<Translation>) : Translation()
//}
//
//object TranslationSerializer : KSerializer<Translation> {
//
//    override val descriptor: SerialDescriptor =
//        buildClassSerialDescriptor("Translation") {
//            element("value", String.serializer().descriptor, isOptional = true)
//            element("param", RemoteParameter.serializer().descriptor, isOptional = true)
//            element("children", Translation.serializer().descriptor, isOptional = true)
//        }
//
//    override fun deserialize(decoder: Decoder): Translation {
//        return decoder.decodeStructure(descriptor) {
//            val value = decodeStringElement(descriptor, 0)
//            if (value != null) {
//                val params = decodeSerializableElement(descriptor, 1, ListSerializer(RemoteParameter.serializer()))
//                Translation.Value(value, params)
//            }
//            else {
//                val children = decodeSerializableElement(descriptor, 2, ListSerializer(Translation.serializer()))
//                Translation.Container(children)
//            }
//        }
//    }
//
//    override fun serialize(encoder: Encoder, value: Translation) {
//        encoder.encodeStructure(descriptor) {
//            when (value) {
//                is Translation.Value -> {
//                    encodeStringElement(descriptor, 0, value.value)
//                    encodeSerializableElement(descriptor, 1, RemoteParameter.serializer(), value.params[0])
//                }
//
//                is Translation.Container -> {
//                    encodeSerializableElement(descriptor, 2, Translation.serializer(), value.children[0])
//                }
//            }
//        }
//    }
//}

@Serializable(with = ColorAsObjectSerializer::class)
data class Color(val rgb: Int){
    override fun toString(): String {
        return "Color(rgb=${rgb.toHexString()})"
    }
}

object ColorAsObjectSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Color") {
            element<Int>("r")
            element<Int>("g")
            element<Int>("b")
        }
    override fun serialize(encoder: Encoder, value: Color) =
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, (value.rgb shr 16) and 0xff)
            encodeIntElement(descriptor, 1, (value.rgb shr 8) and 0xff)
            encodeIntElement(descriptor, 2, value.rgb and 0xff)
        }

    override fun deserialize(decoder: Decoder): Color =
        decoder.decodeStructure(descriptor) {
            var r = -1
            var g = -1
            var b = -1
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> r = decodeIntElement(descriptor, 0)
                    1 -> g = decodeIntElement(descriptor, 1)
                    2 -> b = decodeIntElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            require(r in 0..255 && g in 0..255 && b in 0..255)
            Color((r shl 16) or (g shl 8) or b)
        }
}

class ResourcesParserTest {

    @Test
    fun `test parsing`() {
        val result = Json.decodeFromString<Color>(json2)
        println("Result: $result")
//        val result = Json.decodeFromString<List<Translation>>(json)
//        result.forEach { println("Translation: $it") }
    }
    val json2 = """{
        "r": 255,
        "g": 255,
        "b": 255
    }"""

    val json = """[
        {
            "App.Name": {
                "value": "MoWid"
            }
        },
        {
            "Title": {
                "Home": {
                    "value": "MoWid"
                },
                "Group": {
                    "Add": {
                        "value": "Add group"
                    }
                },
                "Edit": {
                    "value": "Edit group"
                }
            }
        }
    ]"""

}