package com.example.ventilrechneropel.model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ventilrechneropel.MyApp
import com.example.ventilrechneropel.R
import com.example.ventilrechneropel.serialize.AppDatabase
import com.example.ventilrechneropel.serialize.FileManager
import com.example.ventilrechneropel.serialize.MotorSet
import com.example.ventilrechneropel.serialize.UiDeSerializer
import com.example.ventilrechneropel.serialize.UiSerializer
import com.example.ventilrechneropel.type.ValveType
import com.example.ventilrechneropel.ui.parameterclass.PistonSettingParameters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    // Main UI state
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private val kzValues = loadKZValues(MyApp.getContext())
    private val db = AppDatabase.getInstance()

    init{
        setup()
    }

    private fun setup() {
        FileManager.initializeManager()
        loadMotorNames()
        _uiState.value.listCylinders.clear()

        val defaultX = 0.0f // calculated error (h gap)
        val defaultInH = 0.26f
        val defaultOutH = 0.36f

        val defaultKZ = lookupKZ(kzValues,defaultX)

        for (i in 1 until 5) {
            val uiCylinder = UiCylinder(i,defaultInH,defaultOutH,)
            val uiValve = MutableStateFlow(UiValve(uiCylinder, defaultX,defaultKZ,defaultKZ,defaultInH, defaultInH, Color.Gray,
                ValveType.INLET,1))
            val listValveI = mutableListOf<MutableStateFlow<UiValve>>()
            val listValveO = mutableListOf<MutableStateFlow<UiValve>>()
            listValveI.add(uiValve)
            listValveI.add(MutableStateFlow(uiValve.value.copy(idxV = 2, type =  ValveType.INLET)))
            listValveO.add(MutableStateFlow(uiValve.value.copy(idxV = 1, type =  ValveType.OUTLET, presentH = defaultOutH, neededH = defaultOutH)))
            listValveO.add(MutableStateFlow(uiValve.value.copy(idxV = 2, type =  ValveType.OUTLET, presentH = defaultOutH, neededH = defaultOutH)))
            uiCylinder.listInValves = listValveI
            uiCylinder.listOutValves = listValveO
            _uiState.value.listCylinders.add(MutableStateFlow(uiCylinder))
        }
        _uiState.update { it ->
            it.copy(motorName = getResourceString(R.string.new_motor_name))
        }
        _updateBOMList()
    }

    fun getCylinderCount(): Int {
        return _uiState.value.listCylinders.count()
    }

    fun getValvesPerCylinderCamShaftCount(): Int {
        // currently fixed
        return 2
    }

    fun getValve(idxCylinder: Int, idxValve: Int, valveType: ValveType): UiValve {
        if (valveType == ValveType.INLET) {
            uiState.value.listCylinders.forEach { cylinder ->
                if (cylinder.value.idxC == idxCylinder) {
                    cylinder.value.listInValves.forEach { valve ->
                        if (valve.value.idxV == idxValve)
                            return valve.value.copy()
                    }
                }
            }
        } else {
            uiState.value.listCylinders.forEach { cylinder ->
                if (cylinder.value.idxC == idxCylinder) {
                    cylinder.value.listOutValves.forEach { valve ->
                        if (valve.value.idxV == idxValve)
                            return valve.value.copy()
                    }
                }
            }
        }
        throw IllegalArgumentException("No uiValve for idxCylinder=[$idxCylinder] and idxValve=[$idxValve] found!")
    }

    private fun updateValve(idxCylinder: Int, idxValve: Int, uiValve: UiValve) {

        val listCylinders = _uiState.value.listCylinders
        assert(listCylinders.isNotEmpty())

        if (uiValve.type == ValveType.INLET){
            listCylinders.forEach { cylinder ->
                if (cylinder.value.idxC == idxCylinder) {
                        cylinder.value.listInValves.forEach { valve ->
                            if (valve.value.idxV == idxValve) {
                                valve.update { valveIt ->
                                    valveIt.copy(
                                        neededKZ = getNewKZ(uiValve),
                                        presentKZ = uiValve.presentKZ,
                                        neededH = getNewValH(uiValve),
                                        presentH = uiValve.presentH,
                                        valX = getErrorX(
                                            getNewKZ(uiValve),
                                            uiValve.presentKZ,
                                            uiValve.presentH
                                        )
                                    )
                                }
                                return
                            }
                        }
                }
            }
        } else {
            listCylinders.forEach {cylinder ->
                if (cylinder.value.idxC == idxCylinder) {
                    cylinder.value.listOutValves.forEach {valve ->
                        if (valve.value.idxV == idxValve) {
                            valve.update {valveIt ->
                                valveIt.copy(
                                    neededKZ = getNewKZ(uiValve),
                                    presentKZ = uiValve.presentKZ,
                                    neededH = getNewValH(uiValve),
                                    presentH = uiValve.presentH,
                                    valX = getErrorX(
                                        getNewKZ(uiValve),
                                        uiValve.presentKZ,
                                        uiValve.presentH
                                    )
                                )
                            }
                            return
                        }
                    }
                }
            }
        }
        throw IllegalArgumentException("No uiValve for idxCylinder=[$idxCylinder] and idxValve=[$idxValve] found!")
    }

    private fun getErrorX(neededKZ: KZValue, presentKZ: KZValue, presentH: Float): Float {
        val returnValue = (presentH + presentKZ.ds - neededKZ.ds)
        return if (returnValue > 0) returnValue else 0f
    }


    private fun getNewValH(uiValve: UiValve): Float {
        return uiValve.presentKZ.ds + uiValve.presentH - uiValve.neededKZ.ds
    }

    private fun getNewKZ(uiValve: UiValve): KZValue {

        val newKZ: Float = if (uiValve.type == ValveType.INLET) {
            (uiValve.presentKZ.ds + uiValve.presentH) - uiValve.cylinder.inH
        } else {
            (uiValve.presentKZ.ds + uiValve.presentH) - uiValve.cylinder.outH
        }
        return lookupKZ(kzValues,newKZ)
    }

    private fun _onResultMotorNames(names : List<String>) {
        var listMutable : MutableList<MutableStateFlow<String>> = mutableListOf()

        names.forEach() { name ->
            if (name.isNotEmpty())
                listMutable.add(MutableStateFlow(name))
        }

        _uiState.update { it ->
            it.copy(
                listMotorNames = listMutable
            )
        }
    }

    private fun _updateBOMList() {
        // if listTableEntries is empty than create a new one
        // corresponding with one entry for each kzValue
        if (_uiState.value.listTableEntries.isEmpty()) {
            kzValues.forEach() { kzValue ->
                _uiState.value.listTableEntries.add(
                    MutableStateFlow(
                        UiListEntry(
                            kzValue = kzValue.copy(),
                            0,
                            0,
                            0
                        )
                    )
                )
            }
        } else {
            // reset each "countable" entry in the list to default "0" value, so
            // we can create a new list without new objects
            _uiState.value.listTableEntries.forEach() { entry ->
                entry.value.presentKZ = 0
                entry.value.neededKZ = 0
                entry.value.missingKZ = 0
            }
        }

        // iterate over each cylinder and each set of in valves and out valves
        // 1 for each oldKZ add present +1
        // 2. for each newKZ add needed +1
        // 3. calculate the difference between present and needed -> set to missing
        val listCylinders = _uiState.value.listCylinders
        assert(listCylinders.isNotEmpty())
        listCylinders.forEach { cylinder ->
            cylinder.value.listInValves.forEach() { inValve ->
                _uiState.value.listTableEntries.forEach() { tableEntryStateObserver: MutableStateFlow<UiListEntry> ->
                    if (tableEntryStateObserver.value.kzValue.kz == inValve.value.presentKZ.kz) {
                        tableEntryStateObserver.value.presentKZ++
                    }
                    if (tableEntryStateObserver.value.kzValue.kz == inValve.value.neededKZ.kz){
                        tableEntryStateObserver.value.neededKZ++
                    }
                    tableEntryStateObserver.value.missingKZ = tableEntryStateObserver.value.neededKZ - tableEntryStateObserver.value.presentKZ
                }
            }

            cylinder.value.listOutValves.forEach() { outValve ->
                _uiState.value.listTableEntries.forEach() { tableEntryStateObserver: MutableStateFlow<UiListEntry> ->
                    if (tableEntryStateObserver.value.kzValue.kz == outValve.value.presentKZ.kz) {
                        tableEntryStateObserver.value.presentKZ++
                    }
                    if (tableEntryStateObserver.value.kzValue.kz == outValve.value.neededKZ.kz){
                        tableEntryStateObserver.value.neededKZ++
                    }
                    tableEntryStateObserver.value.missingKZ = tableEntryStateObserver.value.neededKZ - tableEntryStateObserver.value.presentKZ
                }
            }
        }
    }

    fun updatePresentValve(valve: UiValve, h: String, kzValue: KZValue) {
        updateValve(valve.cylinder.idxC, valve.idxV, valve.copy(
            presentH = h.toFloat(),
            presentKZ = kzValue)
        )
        _updateBOMList()
        _updateMotor()
    }

    fun kzTableHeader(): List<String> {

        return listOf(
            getResourceString(R.string.kz_value),
            getResourceString(R.string.present),
            getResourceString(R.string.need),
            getResourceString(R.string.missing))
    }

    fun tableRowCount(): Int {
        return _uiState.value.listTableEntries.count()
    }

    fun updatePiston(h: Float, valveType: ValveType, param: PistonSettingParameters) {
        _uiState.value.listCylinders.forEach() { cylinderObserver ->
            if (cylinderObserver.value.idxC == param.idxCylinder) {
                if (valveType == ValveType.INLET) {
                    cylinderObserver.value.inH = h
                } else {
                    cylinderObserver.value.outH = h
                }
            }
        }

        _uiState.value.listCylinders.forEach() { cylinderObserver ->
            if (valveType == ValveType.INLET) {
                for (i in 0 until getValvesPerCylinderCamShaftCount()) {
                    updateValve(
                        cylinderObserver.value.idxC, i + 1,
                        cylinderObserver.value.listInValves[i].value
                    )
                }
            } else {
                for (i in 0 until getValvesPerCylinderCamShaftCount()) {
                    updateValve(
                        cylinderObserver.value.idxC, i + 1,
                        cylinderObserver.value.listOutValves[i].value
                    )
                }
            }
        }
        _updateBOMList()
    }

    private fun _updateMotor() {
        saveMotor(_uiState.value.motorName)
    }

    fun saveMotor(name: String) {

        val valDao = db.motorDao()

        // guard protect against wrong motor name
        if (name == getResourceString(R.string.new_motor_name)) {
            println("saveMotor() guard trigger...")
            return
        }

        var motorSet : MotorSet = MotorSet(
            userName = _uiState.value.userName,
            motorName = name,
            jsonizedMotor = UiSerializer(_uiState).getJsonString()
        )

        viewModelScope.launch {
            valDao.insertAll(motorSet)
            _uiState.update {it ->
                it.copy(
                    motorName = motorSet.motorName
                )
            }
        }

        loadMotorNames()
    }

    fun loadMotor(motorName: String) {
        val motorDao = db.motorDao()

        viewModelScope.launch {
            val motor = motorDao.loadByName(
                userName = _uiState.value.userName,
                motorName = motorName
            )

            if (motor != null) {
                _uiState.update { it.copy(motorName = motor.motorName) }
                var deSerializer = UiDeSerializer(motor.jsonizedMotor)
                deSerializer.deserializeMotor(_uiState)
                _updateBOMList()
            }
        }

        /* val fileName = FileManager.getFileNameFromName(name, FileAction.Load)
         val file = File(MyApp.getContext().filesDir, fileName)
         if (file.exists()) {
             val uiDeSerializer = UiDeSerializer(file.readText())
             uiDeSerializer.deserializeIntoData(_uiState)
             _uiState.value.valveSetName = name
             _updateBOMList()
         }*/
    }

    fun loadMotorNames(){
        var result = mutableListOf("")

        viewModelScope.launch {
            var motorNames : List<MotorSet> = db.motorDao().loadAll()
            motorNames.forEach() { motor ->
                result.add(motor.motorName)
            }
            _onResultMotorNames(result)
        }
    }

    fun checkMotorName(newMotorName : String) {

        viewModelScope.launch {
            val motorLoaded = db.motorDao().loadByName(
                userName = _uiState.value.motorName,
                motorName = newMotorName
            )

            // check if name is standard new
            if ( newMotorName == getResourceString(R.string.new_motor_name) )
            {
                _uiState.update {
                    it.copy(isMotorNameError = true)
                }
                return@launch
            } else {
                _uiState.update {
                    it.copy(isMotorNameError = false)
                }
            }

            if ( motorLoaded != null) {
                _uiState.update {
                    it.copy(isMotorNameError = true)
                    return@launch
                }
            } else {
                _uiState.update {
                    it.copy(isMotorNameError = false)
                }
            }
        }
    }

    fun deleteMotor(motorName: String) {

        viewModelScope.launch {
            var motorSet = MotorSet(
                userName = _uiState.value.userName,
                motorName = motorName,
                jsonizedMotor = "")
            db.motorDao().delete(motorSet)

            setup()
        }
    }

    fun newMotor() {
        setup()
    }
}
