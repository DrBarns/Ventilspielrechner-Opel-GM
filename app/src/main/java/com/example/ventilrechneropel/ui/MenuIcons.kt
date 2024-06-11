package com.example.ventilrechneropel.ui
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ventilrechneropel.model.MainViewModel
import kotlinx.coroutines.launch


@Composable
@Deprecated("Not useful right know, just example code")
fun HamburgerMenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(40.dp), onDraw = {
            val strokeWidth = 4f
            val halfStrokeWidth = strokeWidth / 2
            val halfSize = size.width / 2
            val quarterSize = size.width / 4

            drawCircle(
                color = Color.Cyan,
                radius = halfSize - halfStrokeWidth,
                style = Stroke(strokeWidth)
            )

            rotate(45f) {
                drawRect(
                    color = Color.Cyan,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = halfSize - halfStrokeWidth,
                        y = -quarterSize
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = strokeWidth,
                        height = halfSize + strokeWidth
                    )
                )
            }

            rotate(90f) {
                drawRect(
                    color = Color.Cyan,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = halfSize - halfStrokeWidth,
                        y = -quarterSize
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = strokeWidth,
                        height = halfSize + strokeWidth
                    )
                )
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SATopAppBar( scrollContent: @Composable ( padding:  PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Small Top App Bar")
                }
            )
        },
    ) { innerPadding ->
         scrollContent( innerPadding )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CATopAppBar( title: String, drawerState: DrawerState,  scrollContent: @Composable (padding: PaddingValues) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    val mainViewModel: MainViewModel = viewModel()
    var showNewMotorDialog by remember { mutableStateOf(false) }

    if (showNewMotorDialog) {
        NewMotorDialog(myViewModel = mainViewModel) {
            showNewMotorDialog = false
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showNewMotorDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.apply {
                                if(isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        scrollContent(innerPadding)
    }
}

