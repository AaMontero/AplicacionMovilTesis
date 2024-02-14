package com.example.aplicaciontesis

import android.media.Image
import com.example.aplicaciontesis.dataClass.Vivienda
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File

interface ApiService {
    @GET("obtener-todos")
    fun obtenerViviendas(): Call<List<Vivienda>>


    @Multipart
    @POST("upload")
    fun ingresarImagenPrediccion(@Part file: MultipartBody.Part): Call<ResponseBody>

    @GET("get_cluster/{cluster}")
    fun obtenerClusterSeleccionado(@Path("cluster") cluster: String): Call<ResponseBody>
}