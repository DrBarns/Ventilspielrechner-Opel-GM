package com.example.ventilrechneropel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ventilrechneropel.ui.MainLayoutNavigation
import com.example.ventilrechneropel.ui.ScreenA
import com.example.ventilrechneropel.ui.ScreenB
import com.example.ventilrechneropel.ui.ScreenC
import com.example.ventilrechneropel.ui.theme.VentilRechnerOpelTheme

object NavRoute {
    const val SCREEN_A = "Screen A"
    const val SCREEN_B = "Screen B"
    const val SCREEN_C = "Screen C"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VentilRechnerOpelTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayoutNavigation()
                    // MyNavHost(navHostController = navController)
                }
            }
        }
    }


}

@Composable
fun MyNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavRoute.SCREEN_A
    ) {
        val routeWithArguments  = "${NavRoute.SCREEN_B}/{argumentToPass}"
        val routeWithArgumentsC = "${NavRoute.SCREEN_C}/{argumentToPass}"
        composable(NavRoute.SCREEN_A) {
            ScreenA() {
                navHostController.navigate("${NavRoute.SCREEN_B}/The Argument Passed To B")
            }
        }
        composable(
            routeWithArguments,
            arguments = listOf(navArgument("argumentToPass") {})
        ) {
            ScreenB(it.arguments?.getString("argumentToPass")) {
                navHostController.navigate("${NavRoute.SCREEN_C}/false")
            }
        }
        composable(
            routeWithArgumentsC,
            arguments = listOf(navArgument("argumentToPass") {
                type = NavType.BoolType
            })
        ) {
            ScreenC(it.arguments?.getBoolean("argumentToPas")) {
                navHostController.navigate(NavRoute.SCREEN_A) {
                    popUpTo(NavRoute.SCREEN_A) {
                        inclusive = true
                    }
                }
            }
        }
    }
}

