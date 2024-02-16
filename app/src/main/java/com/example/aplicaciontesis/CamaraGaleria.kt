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
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.squareup.picasso.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

class CamaraGaleria : AppCompatActivity() {
    private lateinit var binding: ActivityCamaraGaleriaBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val CAMERA_CAPTURE_REQUEST_CODE = 101
    private lateinit var file:File
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
    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        val file = Utils.uriToFile(this, uri)
                        if (file != null) {
                            subirImagenAPI(file)
                        }
                    }
                }
            }
            CAMERA_CAPTURE_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    subirImagenAPI(file)
                }
            }
        }
    }
    private fun subirImagenAPI(file :File) {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagenPart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")  // Reemplaza con la URL de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.ingresarImagenPrediccion(imagenPart)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        Log.w("Respuesta", "Esta entrando al  try")
                        val json = response.body()?.string()
                        val prettyJson = GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(json))
                        val jsonObject = JsonParser().parse(json).asJsonObject
                        val clusterValue = jsonObject.get("cluster")?.asString
                        val intent = Intent(this@CamaraGaleria, ActividadRecycler::class.java)
                        intent.putExtra("cluster", clusterValue)
                        startActivity(intent)
                        Log.w("Respuesta", prettyJson)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Respuesta", "Error al procesar la respuesta como JSON")
                    }
                } else {
                    Log.w("Respuesta", "La respuesta no es succesfull")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Respuesta", "Error en la llamada a la API", t)

                if (t is IOException) {
                    // Manejo de errores de red (por ejemplo, falta de conexión)
                    Log.e("Respuesta", "Error de red: ${t.message}")
                } else {
                    // Otros tipos de errores
                    Log.e("Respuesta", "Error desconocido: ${t.message}")
                }
            }
        })
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            it.resolveActivity(packageManager).also { _ ->
                createPhotoFile()
                val photoUri: Uri = FileProvider
                    .getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileProvider", file )
                it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
        }
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST_CODE)
    }

    private fun createPhotoFile(): File {
        val dir  = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG-${System.currentTimeMillis()}_", ".jpg", dir)
        return file ;
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