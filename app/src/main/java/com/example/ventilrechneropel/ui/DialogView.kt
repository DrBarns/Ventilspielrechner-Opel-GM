package com.example.ventilrechneropel.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ventilrechneropel.MyApp
import com.example.ventilrechneropel.R
import com.example.ventilrechneropel.model.KZValue
import com.example.ventilrechneropel.model.MainViewModel
import com.example.ventilrechneropel.model.getResourceColor
import com.example.ventilrechneropel.model.getResourceString
import com.example.ventilrechneropel.model.horzLine
import com.example.ventilrechneropel.model.loadKZValues
import com.example.ventilrechneropel.model.safeStringToFloat
import com.example.ventilrechneropel.type.ValveType
import com.example.ventilrechneropel.ui.parameterclass.PistonSettingParameters
import com.example.ventilrechneropel.ui.parameterclass.ValveSettingParameters
import com.example.ventilrechneropel.ui.theme.typography
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Indicator(count: Int, idx: Int, text: String) {
    Column ( verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxWidth(),
    ) {

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .absolutePadding(top = 2.dp, bottom = 4.dp)
        ) {
            Text( text = text,
                modifier = Modifier
                    .width(100.dp)
                    .padding(start = 5.dp),
                style = typography.h6,
                color = MaterialTheme.colorScheme.onPrimary)
            for (j in 1.. count ) {
                if (j == idx) {
                    Button(
                        onClick = {
                            @Suppress("UNUSED_EXPRESSION")
                            false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(30.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        contentPadding = PaddingValues(0.dp),

                        ) {
                        Text(text = "$j", color = MaterialTheme.colorScheme.onError)
                    }
                } else {
                    Button(
                        onClick = {
                            @Suppress("UNUSED_EXPRESSION")
                            false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                        shape = CircleShape,
                        modifier = Modifier.size(30.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "$j", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun SaveMotorDialog(myViewModel: MainViewModel, onDismiss: () -> Unit) {

    var motorName by remember { mutableStateOf(getResourceString(R.string.new_motor_name)) }
    val mainUiState by myViewModel.uiState.collectAsState()

    AlertDialog(
        title = {
                Text(text = getResourceString(R.string.save_motor_title), style = typography.h5)
        },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                horzLine()
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = motorName,
                    onValueChange = {
                        motorName = it
                        myViewModel.checkMotorName(it)
                    },
                    label = {Text(getResourceString(R.string.motor_name))},
                    textStyle = typography.h6,
                    isError = mainUiState.isMotorNameError)
                horzLine()

                Row(){
                    Column(
                    ) {
                    }
                }
                if (mainUiState.isMotorNameError) {
                    Text(text = getResourceString(R.string.note_motor_name_is_not_vaild))
                }
            }
        },
        confirmButton = {

            Divider()
            TextButton(
                onClick = {
                    myViewModel.saveMotor(motorName)
                    onDismiss()
                          },
                enabled = !mainUiState.isMotorNameError,
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.save),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        onDismissRequest = { onDismiss() }
    )
}

@Composable
fun LoadMotorDialog(myViewModel: MainViewModel, onDismiss: () -> Unit) {

    val myUiModel by myViewModel.uiState.collectAsState()
    myViewModel.loadMotorNames()

    AlertDialog(
        title = {
            Text(text = getResourceString(R.string.load_motor_title), style = typography.h5)
        },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                horzLine()

                val motorNames : List<MutableStateFlow<String>> = myUiModel.listMotorNames
                if (motorNames.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp),
                        text = getResourceString( R.string.no_motor_stored ),
                        style = typography.h6
                    )
                    Text(text = getResourceString( R.string.info_save_motor_first ))
                } else {

                    motorNames.forEach { name ->
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            content = {
                                Text(text = name.collectAsState().value,
                                    style = typography.h6)
                            },
                            onClick = {
                                myViewModel.loadMotor(name.value)
                                onDismiss()
                            }
                        )
                        horzLine()
                    }
                }
            }
        },
        confirmButton = {
            Divider()
            TextButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.close),
                     color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        onDismissRequest = { onDismiss() }
    )
}

@Composable
fun DeleteMotorDialog(myViewModel: MainViewModel, onDismiss: () -> Unit) {

    val myUiModel by myViewModel.uiState.collectAsState()
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var motorName by remember { mutableStateOf("") }
    myViewModel.loadMotorNames()

    if(showConfirmDeleteDialog) {
        ConfirmDeleteDialog(name = motorName, myViewModel = myViewModel ) {
            showConfirmDeleteDialog = false
        }
    }

    AlertDialog(
        title = {
            Text(text = getResourceString(R.string.delete_motor_title), style = typography.h5)
        },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                horzLine()

                val motorNames : List<MutableStateFlow<String>> = myUiModel.listMotorNames
                if (motorNames.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp),
                        text = getResourceString( R.string.no_motor_stored ),
                        style = typography.h6
                    )
                    Text(text = getResourceString( R.string.info_save_motor_first ))
                } else {
                    motorNames.forEach { name ->
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            content = {
                                Text(text = name.collectAsState().value,
                                    style = typography.h6)
                            },
                            onClick = {
                                motorName = name.value
                                showConfirmDeleteDialog = true
                            }
                        )
                        horzLine()
                    }
                }
            }
        },
        confirmButton = {
            Divider()
            TextButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.close),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        onDismissRequest = { onDismiss() }
    )
}

@Composable
fun ConfirmDeleteDialog(name: String, myViewModel: MainViewModel, onDismiss: () -> Unit){

    AlertDialog(
        title = {
            Text(text = getResourceString(R.string.confirm_delete_motor_title), style = typography.h5)

        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
            ) {
                Text(
                    text = getResourceString(R.string.confirm_delete_motor) + ": $name ?",
                    style = typography.h6,
                )
            }
            horzLine()
        },
        confirmButton = {

            TextButton(
                onClick = {
                    myViewModel.deleteMotor(name)
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.delete),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = getResourceString(R.string.close))
            }
        },
        onDismissRequest = { onDismiss() }
    )
}

@Composable
fun ValveSettingDialog(param: ValveSettingParameters, onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            if (param.valveType == ValveType.INLET)
                Text(text = getResourceString(R.string.inlet_valve), style = typography.h4)
            else
                Text(text = getResourceString(R.string.outlet_valve), style = typography.h4)
        },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                horzLine()
                Indicator(
                    count = param.myViewModel.getCylinderCount(),
                    idx =  param.idxCylinder,  getResourceString(R.string.cylinder))
                Indicator(
                    count = param.myViewModel.getValvesPerCylinderCamShaftCount(),
                    idx = param.idxValve, getResourceString(R.string.valve))
                horzLine()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Image(
                        painter = painterResource(R.drawable.valve),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp),
                        alignment = Alignment.Center
                    )
                    Column(
                        modifier = Modifier.padding(start=30.dp)
                    ) {
                        val valve = param.myViewModel.getValve(param.idxCylinder, param.idxValve, param.valveType)


                        // NewValveText(valve.newKZ, valve.newValH)
                        PresentValveText(valve.presentKZ, valve.presentH) { h, kzValue ->
                            param.myViewModel.updatePresentValve(valve, h, kzValue)
                        }
                    }
                }

                Text(text = getResourceString(R.string.note_kz_values),
                    modifier = Modifier
                        .padding(top=20.dp),
                    textAlign = TextAlign.Justify)
            }
        },
        confirmButton = {
            Divider()
            TextButton(
                onClick = { onDismiss() /* do update stuff */ },
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.close),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        onDismissRequest = { onDismiss() }
    )
}

@Composable
fun PistonSettingDialog(param: PistonSettingParameters, onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = getResourceString(R.string.cylinder_settings_title), style = typography.h4)
        },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                horzLine()
                Indicator(count = param.myViewModel.getCylinderCount(), idx =  param.idxCylinder,  getResourceString(R.string.cylinder))
                horzLine()

                Row(){
                    Image(
                        painter = painterResource(R.drawable.valve),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    Column(
                        modifier = Modifier.padding(start=30.dp)
                    ) {
                        var inDH by remember { mutableFloatStateOf(param.inH) }
                        var outDH by remember { mutableFloatStateOf(param.outH) }

                        Divider()
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                label = {
                                    Text(text = getResourceString(R.string.ins_long) )
                                },
                                singleLine = true,
                                readOnly = false,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = inDH.toString(),
                                onValueChange = {
                                    if (it.count{that -> that == '.'} <= 1) {
                                        inDH = it.toFloat()
                                        param.myViewModel.updatePiston(inDH, ValveType.INLET, param)}},
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                visualTransformation = VisualTransformation.None,
                                textStyle = typography.h6
                            )
                        }
                        Divider()
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                label = {
                                    Text(text = getResourceString(R.string.out_long))
                                },
                                singleLine = true,
                                readOnly = false,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = outDH.toString(),
                                onValueChange = {
                                    if (it.count{that -> that == '.'} <= 1) {
                                        outDH = it.toFloat()
                                        param.myViewModel.updatePiston(outDH, ValveType.OUTLET, param)}},
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                visualTransformation = VisualTransformation.None,
                                textStyle = typography.h6
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Divider()
            TextButton(
                onClick = { onDismiss() /* do update stuff */ },
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.close),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        onDismissRequest = { onDismiss() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentValveText(h: KZValue, x: Float, onChange: (h: String, kzValue: KZValue) -> Unit) {

    var expanded by remember { mutableStateOf(false) }
    val kzValues = remember { loadKZValues(MyApp.getContext()) }
    var selectKZValue by remember { mutableStateOf(h) }
    var selectH by remember { mutableFloatStateOf(x) }
    var selectHValue by remember { mutableStateOf(x.toString()) }

    Column {
        Text(text = getResourceString(R.string.present_valve), style = typography.h5, modifier = Modifier
            .padding(top = 10.dp))
        Divider(modifier = Modifier
            .padding(bottom = 5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(1f),
                    readOnly = true,
                    value = selectKZValue.kz,
                    onValueChange = { onChange(it, selectKZValue) },
                    label = { Text("x: ${selectKZValue.ds} - ${selectKZValue.de}") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                    visualTransformation = VisualTransformation.None,
                    textStyle = typography.h6,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }) {
                    kzValues.forEach { dropEntry ->
                        DropdownMenuItem(text = {
                            Row() {
                                Text(text = dropEntry.kz.toString())
                                Text(text = " (${dropEntry.ds}) - (${dropEntry.de})")
                            }
                        }, onClick = {
                            selectKZValue = dropEntry
                            onChange(selectH.toString(), dropEntry)
                            expanded = false
                        })
                    }
                }
            }
        }
        Row (verticalAlignment = Alignment.CenterVertically) {
            TextField(
                label = {
                    Text(text = "h:")
                },
                singleLine = true,
                readOnly = false,
                modifier = Modifier
                    .fillMaxWidth(),
                value = selectHValue,
                onValueChange = {
                    val value = it.safeStringToFloat()
                    if (value != null){
                        selectH = value
                        selectHValue = it
                        onChange(value.toString(), selectKZValue)
                    }},
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                visualTransformation = VisualTransformation.None,
                textStyle = typography.h6,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer)
            )
        }
    }
}

@Composable
@Deprecated("Not in use - but kept for sample")
fun NewValveText(x: KZValue, h: Float) {
    Column {
        Text(text = "New Values (autoset)", style = typography.h5, modifier = Modifier
            .padding(top = 5.dp))
        Divider(modifier = Modifier
            .padding(bottom = 5.dp))
        Row(){
            Text(text = "x:", style = typography.h6 , modifier = Modifier
                .padding(end = 20.dp, bottom = 5.dp))
            Text(text = x.kz, style = typography.h6, color = getResourceColor(R.color.teal_200 ))
        }
        Row() {
            Text(text = "h:", style = typography.h6 , modifier = Modifier
                .padding(end = 20.dp))
            Text(text = "$h", style = typography.h6, color = getResourceColor(R.color.teal_200))
        }
    }
}

@Composable
fun NewMotorDialog(myViewModel: MainViewModel, onDismiss: () -> Unit) {

    AlertDialog(
        title = {
            Text(text = getResourceString(R.string.confirm_new_motor_title), style = typography.h5)

        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
            ) {
                Text(
                    text = getResourceString(R.string.warning_new_motor_deletes_unsaved_motor),
                    style = typography.h6
                )
            }
            horzLine()
        },
        confirmButton = {

            TextButton(
                onClick = {
                    myViewModel.newMotor()
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                Text(text = getResourceString(R.string.new_motor),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = getResourceString(R.string.close))
            }
        },
        onDismissRequest = { onDismiss() }

    )
}
