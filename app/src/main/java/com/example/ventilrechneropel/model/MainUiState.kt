package com.example.ventilrechneropel.model

import androidx.compose.ui.graphics.Color
import com.example.ventilrechneropel.R
import com.example.ventilrechneropel.type.ValveType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//----------------------------------------------------
// Main UI
//----------------------------------------------------
@Serializable
data class MainUiState (
    // Lists
    val listCylinders : MutableList<MutableStateFlow<UiCylinder>> = mutableListOf(),
    val listTableEntries : MutableList<MutableStateFlow<UiListEntry>> = mutableListOf(),
    val listMotorNames : List<MutableStateFlow<String>> = mutableListOf(),

    // Simple Types
    val motorName: String = getResourceString (R.string.new_motor_name),
    val userName: String = getResourceString (R.string.new_user_name),
    val isMotorNameError: Boolean = true) {
}

@Serializable
data class UiCylinder (
    var idxC : Int,
    var inH : Float,
    var outH : Float,
    var listInValves : MutableList<MutableStateFlow<UiValve>> = mutableListOf(),
    var listOutValves : MutableList<MutableStateFlow<UiValve>> = mutableListOf()
)

/**
 * @param cylinder The cylinder this valve is assigned to
 * @param valX ???
 * @param presentKZ The KZ value in use
 * @param neededKZ The KZ value calculated for future use
 * @param neededH The gap calculated for future use
 * @param presentH the gap measured
 * @param color The color this valve should be displayed with
 * @param type The valve type (inlet or outlet)
 * @param idxV The index of this valve
 */
@Serializable
data class UiValve (
    var cylinder : UiCylinder,
    var valX : Float,
    var presentKZ : KZValue,
    var neededKZ : KZValue,
    var neededH : Float,
    var presentH: Float,
    var color : @Contextual Color,
    @SerialName("valveType") var type : ValveType,
    var idxV : Int
)

@Serializable
data class UiListEntry (
    var kzValue: KZValue,
    var presentKZ : Int,
    var neededKZ : Int,
    var missingKZ : Int,
)
