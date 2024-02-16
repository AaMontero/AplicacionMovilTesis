package com.example.aplicaciontesis

import MyAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciontesis.dataClass.Vivienda
import com.example.aplicaciontesis.databinding.ActivityReciclerViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random
class ActividadRecycler : AppCompatActivity() {
    //Definición de variables (XML)
    private lateinit var binding: ActivityReciclerViewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var botonAgregar: FloatingActionButton
    private lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inicialización de las variables
        binding = ActivityReciclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root) // Asignacion del Binding
        recyclerView = binding.recyclerView //Recicler View
        botonAgregar = binding.openCameraButton // Boton para agregar Imagenes
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(emptyList()) // Inicializacion del Adaptador
        recyclerView.adapter = adapter // Asignacion del adapter al Recycler
        val numCluster: String? = intent.getStringExtra("cluster") // Get Parametro extra del Intent
        val numAleat100String  = Random.nextInt(1, 101).toString() // Cluster aleatorio si no se pasa por Intent


        //Sección de carga de elementos para recyclerView
        if (numCluster != null) { // Si se ha subido una imagen
            llamarApiYActualizarViviendas(numCluster)
        } else { // No se ha subido una imagen - Carga inicial
            llamarApiYActualizarViviendas(numAleat100String)
        }


        //Agregar Eventos a los botones
        botonAgregar.setOnClickListener {
            val intent = Intent(this, CamaraGaleria::class.java)
            startActivity(intent) //Intent para la pantalla donde se sube la imagen
        }
    }

    //Funciones
    //Función para llamar al metodo get del API y obtener la lista de elementos
    private fun llamarApiYActualizarViviendas(numeroString: String) {
        try {
            //Llamada al Servicio para acceder al metodo GET y recuperar una lista de Viviendas
            val call = ApiClient.apiService.obtenerClusterSeleccionado(numeroString)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) { // Si la respuesta es exitosa
                        try {
                            val responseBody = response.body() //Recupera el body del Request
                            if (responseBody != null) {
                                val datosJson = responseBody.string() // Recuperar el json como String
                                val viviendas = convertirJsonAVivienda(datosJson) //Convertir el String a una lista de Viviendas
                                adapter.updateData(viviendas) // Agregar la lista al adapter y refrescar
                            }
                        } catch (e: Exception) { // Excepciones
                            e.printStackTrace()
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { //Errores con el API
                    Log.e("ApiCall", "Error: ${t.message}")
                }
            })
        } catch (e: Exception) { //Manejo de errores en conexion de API
            e.printStackTrace()
            Log.e("Respuesta", "Excepción al ejecutar la llamada a la API: ${e.message}")
        }
    }


    //Funcion para obtener una lista de viviendas a partir del JSON
    fun convertirJsonAVivienda(datosJson: String?): List<Vivienda> {
        val viviendas = mutableListOf<Vivienda>() //Lista Vacia
        try {
            if (datosJson != null) { //Si llega un valor no nulo
                val jsonArray = JSONArray(datosJson) //Convierte el Json en un array
                //Recorre el Array para obtener obtener los valores de descripcion, url y img
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
                    // Cambia la descripcion para que los primeros caracteres sean mayusuculas en caso
                    // de tener una longitud mayor a 3
                    val descripcion = json.optString("descripcion", "")
                        .split(" ")
                        .joinToString(" ") {
                            if (it.length > 3) {
                                it.lowercase().replaceFirstChar { char -> char.uppercase() }
                            } else {
                                it.lowercase()
                            }
                        }

                    val img = json.optString("url_img", "")
                    val url = json.optString("url", "")
                    var contiene = false // Determina si la vivienda ya esta en la lista por su URL
                    for(elemento in viviendas){
                        if(elemento.url == url){ // Si el elemento ya esta en la lista pasa al siguiente (No agrega)
                            contiene = true;
                            break
                        }
                    }
                    // Si el elemento no esta en la lista hace una instancia y lo agrega a la lista
                    if(!contiene){
                        viviendas.add(Vivienda(descripcion, img, url))
                    }
                }
            }
        } catch (e: Exception) { //Manejo de errores
            e.printStackTrace()
        }
        return viviendas //Retorna la lista de viviendas con elementos no repetidos
    }


    //Instancia única de Retrofit para la comunicación con la API.
    object RetrofitClient {
        private const val BASE_URL = "http://10.0.2.2:5000/"// URL base de la API

        // Configuracion de la instancia del API
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    // Instancia a la interfaz del API.
    object ApiClient {
        // Instancia de la interfaz de servicio API
        val apiService: ApiService by lazy {
            RetrofitClient.retrofit.create(ApiService::class.java)
        }
    }
}

