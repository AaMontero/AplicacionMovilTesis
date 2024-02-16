package com.example.aplicaciontesis
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @Multipart
    @POST("upload")
    fun ingresarImagenPrediccion(@Part file: MultipartBody.Part): Call<ResponseBody>

    @GET("get_cluster/{cluster}")
    fun obtenerClusterSeleccionado(@Path("cluster") cluster: String): Call<ResponseBody>
}