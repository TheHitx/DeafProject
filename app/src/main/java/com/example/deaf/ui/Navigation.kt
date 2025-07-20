package com.example.deaf.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val audioViewModel: AudioViewModel = viewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onEditClick = { navController.navigate("edit") },
                viewModel = audioViewModel,
                context = context,
                onFileSelected = { filename ->
                    navController.navigate("view_transcription/${filename}")
                }
            )
        }
        composable("edit") {
            TranscriptionEditorScreen(
                audioViewModel = audioViewModel,
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable(
            "view_transcription/{filename}",
            arguments = listOf(navArgument("filename") { type = NavType.StringType })
        ) { backStackEntry ->
            val filename = backStackEntry.arguments?.getString("filename") ?: ""
            TranscriptionViewerScreen(context = context, filename = filename, viewModel = audioViewModel)
        }
    }
}
