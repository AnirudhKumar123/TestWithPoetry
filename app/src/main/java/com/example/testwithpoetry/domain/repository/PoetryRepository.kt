package com.example.testwithpoetry.domain.repository

import com.example.testwithpoetry.NetworkResource
import com.example.testwithpoetry.domain.model.Author
import com.example.testwithpoetry.domain.model.Poem

interface PoetryRepository {
    suspend fun getAuthors(): NetworkResource<List<Author>>
    suspend fun getTitlesByAuthor(authorName: String): NetworkResource<String>
    suspend fun getPoem(authorName: String, title: String): NetworkResource<Poem>
    suspend fun toggleFavourite(author: Author)
}