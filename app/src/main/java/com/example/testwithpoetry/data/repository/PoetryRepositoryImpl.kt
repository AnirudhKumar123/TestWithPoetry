package com.example.testwithpoetry.data.repository

import com.example.testwithpoetry.NetworkResource
import com.example.testwithpoetry.data.local.database.FavouriteAuthor
import com.example.testwithpoetry.data.local.database.FavouriteDao
import com.example.testwithpoetry.domain.model.Author
import com.example.testwithpoetry.domain.model.Poem
import com.example.testwithpoetry.domain.repository.PoetryRepository
import com.example.testwithpoetry.remoteResponses.AuthorsResponse
import com.example.testwithpoetry.remoteResponses.PoemResponse
import com.example.testwithpoetry.remoteResponses.PoemTitleReponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class PoetryRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val favouriteDao: FavouriteDao
) : PoetryRepository {

    override suspend fun getAuthors(): NetworkResource<List<Author>> {
        return try {
            withContext(Dispatchers.IO) {
                val response: AuthorsResponse = client.get("author").body()
                val favourites = favouriteDao.getAllFavourites().first()
                val favouriteName = favourites.map { it.name }.toSet()

                NetworkResource.Success(
                    response.authors.map { author ->
                        Author(author, isFavourite = favouriteName.contains(author))
                    }
                )
            }
        } catch (e: Exception) {
            NetworkResource.Fail(e.parseNetworkError())
        }
    }

    override suspend fun getTitlesByAuthor(authorName: String): NetworkResource<String> {
        return try {
            withContext(Dispatchers.IO) {
                val response: PoemTitleReponse = client.get("author/$authorName/title").body()
                NetworkResource.Success(response.title)
            }
        } catch (e: Exception) {
            NetworkResource.Fail(e.parseNetworkError())
        }
    }

    override suspend fun getPoem(authorName: String, title: String): NetworkResource<Poem> {
        return try {
            withContext(Dispatchers.IO) {
                val response: List<PoemResponse> = client.get("author,title/$authorName;$title").body()
                response.firstOrNull()?.let {
                    NetworkResource.Success(
                        Poem(
                            it.title,
                            it.author,
                            it.lines,
                            it.linecount
                        )
                    ) ?: NetworkResource.Fail("Poem not found")
                }

            }
        } catch (e: Exception) {
            NetworkResource.Fail(e.parseNetworkError())
        }
    }

    override suspend fun toggleFavourite(author: Author) {
        if (author.isFavourite) favouriteDao.delete(FavouriteAuthor(author.name))
        else favouriteDao.insert(FavouriteAuthor(author.name))
    }
}

private fun Exception.parseNetworkError(): String {
    return when (this) {
        is RedirectResponseException -> "Server redirected too many times"
        is ClientRequestException -> "Client error: ${message ?: "Unknown error"}"
        is ServerResponseException -> "Server error: ${message ?: "Unkown error"}"
        is IOException -> "Network connection failed"
        else -> "Unkown error: ${message ?: "No details available"}"
    }
}