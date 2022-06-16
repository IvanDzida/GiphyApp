package com.yolo.network

import retrofit2.http.*

interface GiphyRestApi {

    companion object {
        const val BASE_URL = "https://api.giphy.com/v1/gifs/"
        const val API_KEY = "I7h6oKbGAnsok1AuqP57jmLw6p9zFfOK"
    }

    @GET("trending?api_key=$API_KEY")
    suspend fun getTrending(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): GiphyRestResponse

    @GET("search?api_key=$API_KEY")
    suspend fun search(
        @Query("q") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): GiphyRestResponse
}