package com.example.aplicaciontesis

import MyAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciontesis.dataClass.Vivienda
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class ActividadRecycler : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recicler_view)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(emptyList())
        recyclerView.adapter = adapter
        val call = ApiClient.apiService.obtenerViviendas()

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
                // Tu código de manejo de error aquí
            }
        })
        //Llamada a la función recuperarLista
        //recuperarLista()
    }
    object RetrofitClient {
        private const val BASE_URL = "http://10.0.2.2:5000/api/"

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

