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
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.random.Random

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
        var numAleatorio100 = Random.nextInt(1, 101)
        if (numCluster != null) {
            try {
                val call = ApiClient.apiService.obtenerClusterSeleccionado(numCluster)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            try {
                                Log.w("Respuesta", "La respuesta es exitosa")
                                val responseBody = response.body()
                                if (responseBody != null) {
                                    val datosJson = responseBody.string()
                                    val viviendas = convertirJsonAVivienda(datosJson)
                                    //Log.w("Respuesta", "Datos JSON: $datosJson")
                                    Log.w("Respuesta", "Datos JSON: $viviendas")
                                    adapter.updateData(viviendas)
                                }
                            } catch (e: Exception) {
                                Log.w("Respuesta", "Exeption")
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.w("Respuesta", "La respuesta es Failure")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Respuesta", "Excepción al ejecutar la llamada a la API: ${e.message}")
            }
        } else {

            try {
                val call = ApiClient.apiService.obtenerClusterSeleccionado(numAleatorio100.toString())
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            try {
                                Log.w("Respuesta", "La respuesta es exitosa")
                                val responseBody = response.body()
                                if (responseBody != null) {
                                    val datosJson = responseBody.string()
                                    val viviendas = convertirJsonAVivienda(datosJson)
                                    //Log.w("Respuesta", "Datos JSON: $datosJson")
                                    Log.w("Respuesta", "Datos JSON: $viviendas")
                                    adapter.updateData(viviendas)
                                }
                            } catch (e: Exception) {
                                Log.w("Respuesta", "Exeption")
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.w("Respuesta", "La respuesta es Failure")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Respuesta", "Excepción al ejecutar la llamada a la API: ${e.message}")
            }
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
            if (datosJson != null) {
                Log.w("Respuesta", "El Json es: $datosJson")
                val jsonArray = JSONArray(datosJson)
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
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
                    // Crear un objeto Vivienda y agregarlo a la lista
                    var contiene = false
                    for(elemento in viviendas){
                        if(elemento.url == url){
                            contiene = true;
                            break
                        }
                    }
                    if(contiene == false){
                        viviendas.add(Vivienda(descripcion, img, url))
                    }

                }

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
}

