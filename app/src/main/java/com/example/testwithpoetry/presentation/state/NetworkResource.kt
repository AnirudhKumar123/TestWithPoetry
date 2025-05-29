package com.example.testwithpoetry.presentation.state

sealed class NetworkResource<out T> {
    data class Success<T>(val data: T) : NetworkResource<T>()
    data class Fail<T>(val error: String) : NetworkResource<T>()
    object Loading: NetworkResource<Nothing>()
}