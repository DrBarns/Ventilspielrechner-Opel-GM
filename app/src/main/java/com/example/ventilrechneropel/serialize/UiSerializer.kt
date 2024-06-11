package com.example.ventilrechneropel.serialize

import com.example.ventilrechneropel.model.MainUiState
import com.example.ventilrechneropel.model.UiCylinder
import com.example.ventilrechneropel.model.UiValve
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

class UiSerializer(data: MutableStateFlow<MainUiState>) {

    private val listOfCylinder: MutableList<UiCylinderSerial> = mutableListOf()

    init {
        createSerializable(data)
    }

    private fun createDeSerializable(json: Json, jsonString: String): MutableList<UiCylinderSerial>{
        return json.decodeFromString<MutableList<UiCylinderSerial>>(jsonString)
    }

    private fun createSerializable(data: MutableStateFlow<MainUiState>) {
        data.value.listCylinders.forEach() { cylinder->

            val listInValves : MutableList<UiValveSerial> = mutableListOf()
            val listOutValves : MutableList<UiValveSerial> = mutableListOf()

            val cylinderSerial = UiCylinderSerial (
                idxC = cylinder.value.idxC,
                inH = cylinder.value.inH,
                outH = cylinder.value.outH,
                listInValves =  listInValves,
                listOutValves = listOutValves )

            cylinder.value.listInValves.forEach() { valve ->
                val valveSerial = UiValveSerial(
                    neededKZ = valve.value.neededKZ,
                    color = valve.value.color,
                    type = valve.value.type,
                    presentKZ = valve.value.presentKZ,
                    idxV = valve.value.idxV,
                    neededH = valve.value.neededH,
                    presentH = valve.value.presentH,
                    valX = valve.value.valX)
                cylinderSerial.listInValves.add(valveSerial)
            }

            cylinder.value.listOutValves.forEach() { valve ->
                val valveSerial = UiValveSerial(
                    neededKZ = valve.value.neededKZ,
                    color = valve.value.color,
                    type = valve.value.type,
                    presentKZ = valve.value.presentKZ,
                    idxV = valve.value.idxV,
                    neededH = valve.value.neededH,
                    presentH = valve.value.presentH,
                    valX = valve.value.valX)
                cylinderSerial.listOutValves.add(valveSerial)
            }

            listOfCylinder.add(cylinderSerial)
        }
    }

    fun getJsonString(): String {
        val json = createJsonForUiSerialization()
        return json.encodeToString(listOfCylinder)
    }

    fun deserializeIntoData(jsonString: String, data: MutableStateFlow<MainUiState>) {
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



