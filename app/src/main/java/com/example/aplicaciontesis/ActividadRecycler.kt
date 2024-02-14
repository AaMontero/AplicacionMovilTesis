package com.example.aplicaciontesis

import MyAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciontesis.dataClass.Vivienda
import com.example.aplicaciontesis.databinding.ActivityCamaraGaleriaBinding
import com.example.aplicaciontesis.databinding.ActivityReciclerViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ActividadRecycler : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityReciclerViewBinding
    private lateinit var botonAgregar: FloatingActionButton
    private lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReciclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = binding.recyclerView
        botonAgregar = binding.openCameraButton
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(emptyList())
        recyclerView.adapter = adapter
        val call = ApiClient.apiService.obtenerViviendas()

        var numCluster: String? = intent.getStringExtra("cluster")
        var datosObtenidos: List<Vivienda> = emptyList()
        if (numCluster != null) {
            try {
                Log.w("Respuesta", "El cluster que llega es: $numCluster")

                // Verificar que apiService no sea nulo
                ApiClient.apiService?.let { apiService ->
                    val call = apiService.obtenerClusterSeleccionado(numCluster)
                    val response = call.execute()

                    if (response.isSuccessful) {
                        val datosJson = response.body()?.string()

                        // Manejar el caso donde datosJson sea nulo
                        if (datosJson != null) {
                            val viviendas: List<Vivienda> = convertirJsonAVivienda(datosJson)
                            Log.w("Respuesta", "El Json es: $datosJson")
                            datosObtenidos = viviendas
                            Log.w("Respuesta", "La respuesta es exitosa")
                        } else {
                            Log.w("Respuesta", "El cuerpo de la respuesta es nulo")
                        }
                    } else {
                        Log.w("Respuesta", "La respuesta no fue exitosa: ${response.code()}")
                    }
                } ?: run {
                    Log.w("Respuesta", "apiService es nulo")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Respuesta", "Excepción al ejecutar la llamada a la API: ${e.message}")
            }
        } else {
            Log.w("Respuesta", "No se pudo ejecutar: numCluster es nulo")
        }


        botonAgregar.setOnClickListener {
            val intent = Intent(this, CamaraGaleria::class.java)
            startActivity(intent)
        }

        call.enqueue(object : Callback<List<Vivienda>> {
            override fun onResponse(call: Call<List<Vivienda>>, response: Response<List<Vivienda>>) {
                if (response.isSuccessful) {
                    val viviendas = response.body()
                    if (viviendas != null) {
                        adapter.updateData(viviendas)
                    }
                }
            }
            override fun onFailure(call: Call<List<Vivienda>>, t: Throwable) {

            }
        })
    }
    fun convertirJsonAVivienda(datosJson: String?): List<Vivienda> {
        val viviendas = mutableListOf<Vivienda>()

        try {
            // Verificar si el JSON no es nulo
            if (datosJson != null) {
                val json = JSONObject(datosJson)

                // Obtener los valores del JSON
                val descripcion = json.optString("descripcion", "")
                val img = json.optString("url_img", "")
                val url = json.optString("url", "")

                // Crear un objeto Vivienda y agregarlo a la lista
                viviendas.add(Vivienda(descripcion, img, url))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return viviendas
    }
    object RetrofitClient {
        private const val BASE_URL = "http://10.0.2.2:5000/"
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    object ApiClient {
        val apiService: ApiService by lazy {
                    RetrofitClient.retrofit.create(ApiService::class.java)
        }
    }
    /*private fun recuperarLista() {

        val baseUrl = "http://10.0.2.2:5000/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = ApiClient.apiService.obtenerViviendas()
        call.enqueue(object : Callback<Vivienda> {
            override fun onResponse(call: Call<Vivienda>, response: Response<Vivienda>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    // Handle the retrieved post data
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<Vivienda>, t: Throwable) {
                // Handle failure
            }
        })
    }*/
}

