package com.example.testwithpoetry.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PoemTitleResponse(
    val title:  String
)