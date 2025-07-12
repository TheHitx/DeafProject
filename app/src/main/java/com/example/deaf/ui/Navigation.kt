package com.example.deaf.ui

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = AudioViewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onEditClick = {
                    navController.navigate("edit")
                },
                viewModel = viewModel,
                context = context
            )
        }
        composable("edit") {
            TranscriptionEditorScreen(
                context = context,
                viewModel = viewModel
            )
        }
    }
}
