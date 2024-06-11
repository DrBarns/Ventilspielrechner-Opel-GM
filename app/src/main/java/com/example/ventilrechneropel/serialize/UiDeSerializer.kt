package com.example.ventilrechneropel.serialize

import com.example.ventilrechneropel.model.MainUiState
import com.example.ventilrechneropel.model.UiCylinder
import com.example.ventilrechneropel.model.UiValve
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

class UiDeSerializer(private var jsonString: String) {

    private fun createDeSerializable(json: Json, jsonString: String): MutableList<UiCylinderSerial>{
        return json.decodeFromString<MutableList<UiCylinderSerial>>(jsonString)
    }

    fun deserializeMotor(data: MutableStateFlow<MainUiState>) {
        val json = createJsonForUiSerialization()
        val listOfCylinderSerial = createDeSerializable(json, jsonString)

        data.value.listTableEntries.clear()
        data.value.listCylinders.clear()

        listOfCylinderSerial.forEach() { uiCylinderSerial ->
            var cylinder: UiCylinder = UiCylinder(
                inH = uiCylinderSerial.inH,
                outH = uiCylinderSerial.outH,
                idxC = uiCylinderSerial.idxC
            )

            uiCylinderSerial.listInValves.forEach() { uiValveSerial ->
                var valve: UiValve = UiValve(
                    cylinder = cylinder,
                    valX = uiValveSerial.valX,
                    presentH = uiValveSerial.presentH,
                    neededH = uiValveSerial.neededH,
                    presentKZ = uiValveSerial.presentKZ,
                    neededKZ = uiValveSerial.neededKZ,
                    idxV = uiValveSerial.idxV,
                    type = uiValveSerial.type,
                    color = uiValveSerial.color,
                )
                cylinder.listInValves.add(MutableStateFlow(valve))
            }

            uiCylinderSerial.listOutValves.forEach() { uiValveSerial ->
                var valve: UiValve = UiValve(
                    cylinder = cylinder,
                    valX = uiValveSerial.valX,
                    presentH = uiValveSerial.presentH,
                    neededH = uiValveSerial.neededH,
                    presentKZ = uiValveSerial.presentKZ,
                    neededKZ = uiValveSerial.neededKZ,
                    idxV = uiValveSerial.idxV,
                    type = uiValveSerial.type,
                    color = uiValveSerial.color,
                )
                cylinder.listOutValves.add(MutableStateFlow(valve))
            }
            data.value.listCylinders.add(MutableStateFlow(cylinder))
        }
    }
}

private fun createJsonForUiSerialization(): Json {
    val module = SerializersModule { contextual(ColorSerializer) }
    val json = Json {
        serializersModule = module
        prettyPrint = true
    }
    return json
}



