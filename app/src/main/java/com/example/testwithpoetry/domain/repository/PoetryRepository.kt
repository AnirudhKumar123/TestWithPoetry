package com.example.testwithpoetry.domain.repository

import com.example.testwithpoetry.presentation.state.NetworkResource
import com.example.testwithpoetry.data.local.model.Author
import com.example.testwithpoetry.data.local.model.Poem

interface PoetryRepository {
    suspend fun getAuthors(): NetworkResource<List<Author>>
    suspend fun getTitlesByAuthor(authorName: String): NetworkResource<List<String>>
    suspend fun getPoem(authorName: String, title: String): NetworkResource<Poem>
    suspend fun toggleFavourite(author: Author)
}