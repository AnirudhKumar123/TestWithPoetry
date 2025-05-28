package com.example.testwithpoetry.domain.model

data class Poem(
    val title: String,
    val content: List<String>,
    val lines: List<String>,
    val linecount: String
)
