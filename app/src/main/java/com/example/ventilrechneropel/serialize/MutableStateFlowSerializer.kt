package com.example.ventilrechneropel.serialize

// MutableStateFlowSerializer import

// Json import
import com.example.ventilrechneropel.model.MainUiState
import com.example.ventilrechneropel.model.UiCylinder
import com.example.ventilrechneropel.model.UiListEntry
import com.example.ventilrechneropel.model.UiValve
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Deprecated("Does not work, reason seems to be that generalization is not supported in SerializersModule.")
class MutableStateFlowSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<MutableStateFlow<T>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MutableStateFlow") {
        element("value", dataSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MutableStateFlow<T>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, dataSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): MutableStateFlow<T> {
        return decoder.decodeStructure(descriptor) {
            val value = decodeSerializableElement(descriptor, 0, dataSerializer)
            MutableStateFlow(value)
        }
    }
}

//////////////////////////////////////////////////////
// Example Usage
// Does not work but good starting point may be
//////////////////////////////////////////////////////
private fun createJsonTest(): Json {

    val moduleX = SerializersModule {
        contextual(MutableStateFlowSerializer(UiCylinder.serializer()))
        contextual(MutableStateFlowSerializer(UiListEntry.serializer()))
        contextual(MutableStateFlowSerializer(UiValve.serializer()))
        contextual(MutableStateFlowSerializer(MainUiState.serializer())) // Register for MainUiState*/
    }

    val moduleY = SerializersModule {
        polymorphic(Any::class) {
            subclass(MainUiState.serializer())
            subclass(UiCylinder.serializer())
            subclass(UiListEntry.serializer())
            subclass(UiValve.serializer())
        }
    }

    val json = Json {
        serializersModule = moduleX // or moduleY (both known not to work)
        prettyPrint = true
    }
    return json
}

private fun mainTest() {
    val _uiState = MutableStateFlow(MainUiState())
    val json = Json { prettyPrint= true}
    val uiStateString = json.encodeToString(MutableStateFlowSerializer(MainUiState.serializer()), _uiState)
    println("TTTTT $uiStateString")
}