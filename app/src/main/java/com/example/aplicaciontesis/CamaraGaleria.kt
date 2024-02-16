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
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
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
    //Definicion de las variables (XML)
    private lateinit var binding: ActivityCamaraGaleriaBinding
    private lateinit var file:File
    private lateinit var btnCamera: ImageButton
    private lateinit var btnGaleria: ImageButton
    //Inicialización de los codigos de permiso para la cámara y la galería
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val CAMERA_CAPTURE_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inicializacion de los recursos y del binding
        binding = ActivityCamaraGaleriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnCamera = binding.btnCamara
        btnGaleria = binding.btnGaleria
        //El btn de camara en caso de no tener permisos para el acceso los solicita, caso contrario abre la camara
        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                openCamera()
            }
        }
        //El boton de galería la abre para poder seleccionar la imagen de interes
        btnGaleria.setOnClickListener {
            seleccionarImagen()
        }
    }


    //FUNCIONES
    //Inicializa un intent para acceder a los medios (Galeria) y arroja un codigo cuando se lanza(100)
    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }
    //Dependiendo del codigo de respuesta que se arroje se ejecuta un bloque de codigo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> { // Si el codigo es 100 (Galería)
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        val file = Utils.uriToFile(this, uri) // Se obtiene el archivo del URI
                        if (file != null) {
                            subirImagenAPI(file) // Se sube la imagen a la solicitud POST del API
                        }
                    }
                }
            }
            CAMERA_CAPTURE_REQUEST_CODE -> { // Si el codigo es 101 (Cámara)
                if (resultCode == RESULT_OK) {
                    subirImagenAPI(file) // Se sube el archivo al POST del API
                }
            }
        }
    }
    //Funcion para subir la imagen al API
    private fun subirImagenAPI(file :File) {

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull()) // Convierte el archivo en un Body para pasarlo a un request
        //Crear un archivo de tipo Multipart, que contiene un nombre, un nombre de archivo y un body
        val imagenPart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        // Conectar con la libería de retrofit para configurar la instancia
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")  // La URL de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java) //Crea una interfaz del API de Servicio
        val call = apiService.ingresarImagenPrediccion(imagenPart) // Se llama al método POST del Servicio
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val json = response.body()?.string() // Se convierte al body del request en un JSON
                        val jsonObject = JsonParser().parse(json).asJsonObject //Se crea una estructura de arbol Json
                        val clusterValue = jsonObject.get("cluster")?.asString // Se obtiene el valor del campo Cluster
                        val intent = Intent(this@CamaraGaleria, ActividadRecycler::class.java) //Se inicializa el Intent
                        intent.putExtra("cluster", clusterValue) // Se agrega el valor de Cluster
                        startActivity(intent) // Se inicia el Intent
                    } catch (e: Exception) { //Manejo de errores
                        e.printStackTrace()
                    }
                } else { //Manejo de errores
                    val errorBody = response.errorBody()?.string() ?: "Error"
                    val errorMessage = "La solicitud no fue exitosa. Código: ${response.code()}, Mensaje: $errorBody"
                    Log.w("Respuesta", errorMessage)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { // Maneja de errores en el API
                    Log.e("Respuesta", "Error: ${t.message}")
            }
        })
    }
    //Funcion para abrir la cámara
    private fun openCamera() {
        // Se crea un intent que va a la capturadora de imagen
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            it.resolveActivity(packageManager).also { _ ->
                createPhotoFile() // Se llama a la funcion para crear un File Temp
                val photoUri: Uri = FileProvider // Se obtiene un URI del archivo temporal creado
                    .getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileProvider", file )
                it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri) // Se guarda la URI como un extra del Intent
            }
        }
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST_CODE) // Se ejecuta el llamado y se arroja el codigo (101)
    }
    //Funcion para crear un archivo temporal
    private fun createPhotoFile(): File {
        val dir  = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!! //Se establece la ruta, se valida que no sea nula
        file = File.createTempFile("IMG-${System.currentTimeMillis()}_", ".jpg", dir) //Se establece el prefijo, sufico y directorio del arc. temporal
        return file ; // Se retorna el archivo temporal
    }
    //Manejan la solicitud de permisos para la camara
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera() //En caso de tener permisos se abre la cámara
            } else { // Caso contrario se crea una alerta que permite dar permisos a la cámara
                AlertDialog.Builder(this)
                    .setTitle("Permisos requeridos")
                    .setMessage("Esta aplicación necesita acceso a la cámara para funcionar correctamente. Por favor, otorga los permisos en la configuración de la aplicación.")
                    .setPositiveButton("Ir a configuración") { _, _ ->
                        abrirConfiguracionDeAplicacion() // Permite brindar las opciones de configuración manualmente
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                    }
                    .show()
            }
        }
    }
    // Se llama a un Intent que permite abrir las configuraciones de la aplicacion.
    private fun abrirConfiguracionDeAplicacion() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent) // Se lanza la actividad para realizar las configuracioens
    }
}