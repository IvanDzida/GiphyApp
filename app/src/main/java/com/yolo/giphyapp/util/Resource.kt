package com.yolo.giphyapp.util

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null): Resource<T>(data, throwable)
}