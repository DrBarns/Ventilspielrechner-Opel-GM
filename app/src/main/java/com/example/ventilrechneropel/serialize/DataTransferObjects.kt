package com.example.ventilrechneropel.serialize

import androidx.compose.ui.graphics.Color
import com.example.ventilrechneropel.model.KZValue
import com.example.ventilrechneropel.type.ValveType
import kotlinx.serialization.Serializable

@Serializable
data class MainUiStateSerial (
    val listCylinders: MutableList<UiCylinderSerial> = mutableListOf(),
    /*val listTableEntries: MutableList<UiListEntry> = mutableListOf()*/
)

@Serializable
data class UiCylinderSerial (
    var idxC: Int,
    var inH: Float,
    var outH: Float,
    var listInValves: MutableList<UiValveSerial> = mutableListOf(),
    var listOutValves: MutableList<UiValveSerial> = mutableListOf()
)

@Serializable
data class UiValveSerial (
    var valX: Float,
    var presentKZ: KZValue,
    var neededKZ: KZValue,
    var neededH: Float,
    var presentH: Float,
    @Serializable(with = ColorSerializer::class) var color: Color,
    var type: ValveType,
    var idxV: Int
)