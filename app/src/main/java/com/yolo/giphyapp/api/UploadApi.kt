package com.yolo.giphyapp.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {

    companion object {
        private const val BASE_URL = "https://upload.giphy.com/v1/"
        const val API_KEY = "I7h6oKbGAnsok1AuqP57jmLw6p9zFfOK"

        operator fun invoke() : UploadApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadApi::class.java)
        }
    }

    @Multipart
    @POST("gifs?api_key=$API_KEY")
    fun uploadVideo(
        @Part video : MultipartBody.Part,
        @Part("file") file: RequestBody,
    ) : Call<UploadResponse>
}