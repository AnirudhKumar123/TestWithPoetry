package com.example.testwithpoetry.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthorsResponse(
    val authors: List<String>
)