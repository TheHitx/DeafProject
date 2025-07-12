package com.example.deaf.ui

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deaf.viewmodel.AudioViewModel
import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED


class MainActivity : ComponentActivity() {


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        val storageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true

        if (!audioGranted || !storageGranted) {
            Toast.makeText(this, "Permisos necesarios no otorgados", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionsIfNecessary()

        setContent {
            val context = this
            val audioViewModel: AudioViewModel = viewModel()

            MainScreen(
                onEditClick = {
                    // Aquí puedes agregar lo que quieras que pase al pulsar el botón Editar
                    // Por ejemplo, navegar a la pantalla de edición, pero si aún no tienes navegación,
                    // déjalo vacío o un Toast
                    Toast.makeText(context, "Editar transcripción", Toast.LENGTH_SHORT).show()
                },
                viewModel = audioViewModel,
                context = context
            )
        }
    }

    private fun requestPermissionsIfNecessary() {
        val audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (audioPermission != PERMISSION_GRANTED || storagePermission != PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
}
