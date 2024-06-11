package com.example.ventilrechneropel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ventilrechneropel.R
import com.example.ventilrechneropel.model.MainViewModel
import com.example.ventilrechneropel.model.UiCylinder
import com.example.ventilrechneropel.model.UiValve
import com.example.ventilrechneropel.model.formatString
import com.example.ventilrechneropel.model.getResourceString
import com.example.ventilrechneropel.type.ValveType
import com.example.ventilrechneropel.ui.parameterclass.PistonSettingParameters
import com.example.ventilrechneropel.ui.parameterclass.ValveSettingParameters
import com.example.ventilrechneropel.ui.theme.typography
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun InletValveButton(valve: UiValve, onClick: (valve: UiValve) -> Unit, modifier: Modifier = Modifier) {

    Button(
        onClick = { onClick(valve) },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        shape = CircleShape,
        modifier= modifier.size(100.dp),
    ) {
        Column {

            Text(
                text = "e: ${valve.presentH.formatString(2)}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary,
                softWrap = false

            )
            Text(
                text = "${valve.presentKZ.kz}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary,
                softWrap = false

                )
            Divider()
            Text(
                text = "n: ${valve.valX.formatString(2)}",
                modifier = Modifier,
                color = Color.Cyan,
                softWrap = false
            )
            Text(
                text = "${valve.neededKZ.kz}",
                modifier = Modifier,
                color = Color.Cyan,
                softWrap = false
            )

        }
    }
}

@Composable
fun OutletValveButton(valve: UiValve, onClick: (valve: UiValve) -> Unit, modifier: Modifier = Modifier) {

    Button(onClick = { onClick(valve) },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
        shape = CircleShape,
        modifier= modifier.size(100.dp),
    ) {
        Column {
            Text(
                text = "e: ${valve.presentH.formatString(2)}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onTertiary,
                softWrap = false

            )
            Text(
                text = "${valve.presentKZ.kz}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onTertiary,
                softWrap = false

            )
            Divider()
            Text(
                text = "n: ${valve.valX.formatString(2)}",
                modifier = Modifier,
                color = Color.Cyan,
                softWrap = false
            )
            Text(
                text = "${valve.neededKZ.kz}",
                modifier = Modifier,
                color = Color.Cyan,
                softWrap = false
            )
        }
    }
}

@Composable
fun PistonButton(cylinder: UiCylinder, onClick: () -> Unit, modifier: Modifier = Modifier) {

    Button(onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
        shape = CircleShape,
        modifier= modifier.size(200.dp)
    ) {
        Column( verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = getResourceString(R.string.cylinder)+ ": ${cylinder.idxC}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onSecondary,
                style = typography.h6
            )
            Text(
                text = getResourceString(R.string.ins) + " ${cylinder.inH.formatString(2)}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onSecondary

            )
            Text(
                text = getResourceString(R.string.out) + " ${cylinder.outH.formatString(2)}",
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onSecondary

            )
        }
    }
}


@Composable
fun MainLayoutNavigation() {
    val myViewModel: MainViewModel = viewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if(showSaveDialog) {
        SaveMotorDialog(myViewModel) {
            showSaveDialog = false
        }
    }
    if(showLoadDialog) {
        LoadMotorDialog(myViewModel) {
            showLoadDialog = false
        }
    }

    if(showDeleteDialog) {
        DeleteMotorDialog(myViewModel) {
            showDeleteDialog = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(getResourceString(R.string.menu), modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = getResourceString(R.string.save)) },
                    selected = false,
                    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    onClick = { showSaveDialog = true }
                )
                NavigationDrawerItem(
                    label = { Text(text = getResourceString(R.string.load)) },
                    selected = false,
                    icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    onClick = { showLoadDialog = true  }
                )/* Drawer content */
                NavigationDrawerItem(
                    label = { Text(text = getResourceString(R.string.delete)) },
                    selected = false,
                    icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    onClick = { showDeleteDialog = true  }
                )/* Drawer content */
                /*NavigationDrawerItem(
                    label = { Text(text = getResourceString(R.string.preferences)) },
                    selected = false,
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    onClick = { *//*TODO*//* }
                )*//* Drawer content */
                /* Drawer content */
            }
        },
    ) {
        CATopAppBar(getResourceString(R.string.title_name), drawerState) { padding ->
            MainLayout(padding, myViewModel)
        }
    }
}

@Composable
fun     MainLayout(padding: PaddingValues,
               myViewModel: MainViewModel){

    val mainUiState by myViewModel.uiState.collectAsState()
    //Here we are using power of making simple data classes act as stateful when using `by state`
    var showValveDialogViaStateUpdate by remember { mutableStateOf(false) }
    var showPistonDialogViaStateUpdate by remember { mutableStateOf(false) }
    var selectedValve by remember { mutableStateOf(ValveSettingParameters(0,0, myViewModel, ValveType.INLET )) }
    var selectedPiston by remember { mutableStateOf(PistonSettingParameters(0, 0.2f, 0.3f,  myViewModel)) }

    // if state of 'showDialogViaStateUpdate' changes to true it shows
    // dialog passing state as false for dismiss
    if (showValveDialogViaStateUpdate) {
        ValveSettingDialog(selectedValve) { showValveDialogViaStateUpdate = false }
    }

    if (showPistonDialogViaStateUpdate) {
        PistonSettingDialog(param = selectedPiston) {showPistonDialogViaStateUpdate = false }
    }

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(paddingValues = padding)
        .verticalScroll(rememberScrollState()) // find the infinity maximum height before applying
        .safeDrawingPadding()
            ){

        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = mainUiState.motorName,
                style = typography.h6,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp))
        }

        //Column Start
        Column (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ){
            Divider()
            // Inlet Valves
            Row (modifier = Modifier
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                for (uiCylinder in mainUiState.listCylinders) {
                    var colCylinder = uiCylinder.collectAsState()
                    for (uiValve in colCylinder.value.listInValves) {
                        var colValve = uiValve.collectAsState()
                        InletValveButton(
                            colValve.value,
                            onClick = {
                                showValveDialogViaStateUpdate = true
                                selectedValve = selectedValve.copy(
                                    idxCylinder = colValve.value.cylinder.idxC,
                                    idxValve = colValve.value.idxV,
                                    valveType = ValveType.INLET
                                )
                            },
                        )
                    }
                }
            }
            // Piston
            Row (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                for (uiCylinder in mainUiState.listCylinders) {
                    var colCylinder = uiCylinder.collectAsState()
                    PistonButton(
                        colCylinder.value,
                        onClick = {
                            showPistonDialogViaStateUpdate = true
                            selectedPiston = selectedPiston.copy(
                                idxCylinder = colCylinder.value.idxC,
                                inH = colCylinder.value.inH,
                                outH = colCylinder.value.outH
                            )
                        },
                    )
                }
            }
            // Outlet Valves
            Row (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ){
                for (uiCylinder in mainUiState.listCylinders) {
                    var colCylinder = uiCylinder.collectAsState()
                    for (uiValve in colCylinder.value.listOutValves) {
                        var colValve = uiValve.collectAsState()
                        OutletValveButton(
                            colValve.value,
                            onClick = {
                                showValveDialogViaStateUpdate = true
                                /*myViewModel.onValveButton_click(uiValve)*/
                                /*onNavigation()*/
                                selectedValve = selectedValve.copy(
                                    idxCylinder = colValve.value.cylinder.idxC,
                                    idxValve = colValve.value.idxV,
                                    valveType = ValveType.OUTLET
                                )
                            },
                        )
                    }
                }
            }
        // Column END
        }
        // List View

        Row (modifier = Modifier
            .padding(top = 20.dp)
            .height(600.dp)) {
            Column {
                Text(text = "BOM", style = typography.h6)
                // Inside your Composable function
                TableViewWithHeaderKZ(myViewModel = myViewModel)
            }
        }
    }
}

@Composable
fun <T> getAsStateFlow(obj: MutableStateFlow<T>): T {
    var objState = obj.collectAsState<T>()
    return objState.value
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainLayout(PaddingValues(10.dp), viewModel())
}