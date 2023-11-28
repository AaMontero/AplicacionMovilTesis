package com.example.aplicaciontesis

import com.example.aplicaciontesis.dataClass.Vivienda
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("obtener-todos")
    fun obtenerViviendas(): Call<List<Vivienda>>
}