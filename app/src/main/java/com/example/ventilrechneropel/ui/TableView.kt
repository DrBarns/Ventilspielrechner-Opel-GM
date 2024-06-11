package com.example.ventilrechneropel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ventilrechneropel.model.KZValue
import com.example.ventilrechneropel.model.MainViewModel
import com.example.ventilrechneropel.model.UiListEntry
import kotlin.reflect.full.memberProperties

@Composable
fun TableView(data: List<List<String>>) {
    LazyColumn {
        items(data.size) { rowIndex ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.DarkGray)) {
                data[rowIndex].forEach { cellValue ->
                    Text(
                        text = cellValue,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TableViewWithHeader(header: List<String>, data: List<List<String>>) {
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)) {
            header.forEach { columnHeader ->
                Text(
                    text = columnHeader,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )
            }
        }
        TableView(data = data)
    }
}


@Composable
fun TableViewKZ(myViewModel: MainViewModel) {
    var mainUiState = myViewModel.uiState.collectAsState()
    LazyColumn (
        modifier = Modifier
            .background(Color.Cyan)
    ) {
        items(myViewModel.tableRowCount()) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                var tableEntry = mainUiState.value.listTableEntries[rowIndex].collectAsState()
                val properties = listOf("kzValue", "presentKZ", "neededKZ", "missingKZ")

                if (tableEntry.value.presentKZ == 0 && tableEntry.value.neededKZ == 0) return@Row

                properties.forEach { prop ->
                    val propertyValue = UiListEntry::class.memberProperties
                        .firstOrNull { it.name == prop }
                        ?.get(tableEntry.value)


                    var textValue = "no value"
                    when (prop) {
                        "kzValue" -> {
                            val kzvalue = propertyValue as KZValue
                            textValue = "${kzvalue.kz} (${kzvalue.ds}) - (${kzvalue.de})"
                        }
                        "missingKZ" -> {
                            val missingCount = propertyValue as Int
                            if (missingCount < 0) {
                                textValue = "0 ($missingCount)"
                            } else {
                                textValue = propertyValue.toString()
                            }
                        }
                        else  -> {
                            textValue =  propertyValue.toString()
                        }
                    }
                    when (prop) {
                        "kzValue" -> {
                            Text(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                text = textValue,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(0.7f),
                                softWrap = false
                            )
                        } else -> {
                            Text(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                text = textValue,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(0.15f)
                            )
                    }
                    }
                }
            }
        }
    }
}

@Composable
fun TableViewWithHeaderKZ(myViewModel: MainViewModel) {
    var first = true
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)) {
            myViewModel.kzTableHeader().forEach { columnHeader ->
                if (first) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = columnHeader,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.7f),
                        softWrap = false
                    )
                    first = false
                } else {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = columnHeader,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.15f),
                        softWrap = false
                    )
                }
            }
        }
        TableViewKZ(myViewModel = myViewModel)
    }
}