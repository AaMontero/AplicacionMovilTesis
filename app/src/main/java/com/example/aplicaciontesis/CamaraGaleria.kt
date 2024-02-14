package com.example.aplicaciontesis

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aplicaciontesis.databinding.ActivityCamaraGaleriaBinding
import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AlertDialog

class CamaraGaleria : AppCompatActivity() {
    private lateinit var binding: ActivityCamaraGaleriaBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamaraGaleriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnCamera = binding.btnCamara
        val btnGaleria = binding.btnGaleria
        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                openCamera()
            }
        }
        btnGaleria.setOnClickListener {
            seleccionarImagen()
        }
    }
    fun seleccionarImagen(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 100)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Permisos requeridos")
                    .setMessage("Esta aplicación necesita acceso a la cámara para funcionar correctamente. Por favor, otorga los permisos en la configuración de la aplicación.")
                    .setPositiveButton("Ir a configuración") { _, _ ->
                        abrirConfiguracionDeAplicacion()
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                    }
                    .show()
            }
        }
    }
    private fun abrirConfiguracionDeAplicacion() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}