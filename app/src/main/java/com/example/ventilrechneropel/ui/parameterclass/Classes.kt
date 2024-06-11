package com.example.ventilrechneropel.ui.parameterclass

import com.example.ventilrechneropel.model.MainViewModel
import com.example.ventilrechneropel.type.ValveType


data class ValveSettingParameters(
    val idxCylinder: Int,
    val idxValve: Int,
    val myViewModel: MainViewModel,
    val valveType: ValveType
)


data class PistonSettingParameters(
    val idxCylinder: Int,
    val inH: Float,
    val outH: Float,
    val myViewModel: MainViewModel
)

