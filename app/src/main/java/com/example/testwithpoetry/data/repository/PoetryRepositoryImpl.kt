package com.example.testwithpoetry.data.repository

import android.util.Log
import com.example.testwithpoetry.presentation.state.NetworkResource
import com.example.testwithpoetry.data.local.database.FavouriteAuthor
import com.example.testwithpoetry.data.local.database.FavouriteDao
import com.example.testwithpoetry.data.local.model.Author
import com.example.testwithpoetry.data.local.model.Poem
import com.example.testwithpoetry.domain.repository.PoetryRepository
import com.example.testwithpoetry.data.remote.model.AuthorsResponse
import com.example.testwithpoetry.data.remote.model.PoemResponse
import com.example.testwithpoetry.data.remote.model.PoemTitleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.example.testwithpoetry.data.local.database.AuthorDao

@Singleton
class PoetryRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val favouriteDao: FavouriteDao,
    private val authorDao: AuthorDao
) : PoetryRepository {

    companion object {
        private const val CACHE_DURATION_MS = 5 * 60 * 1000L
    }

    override fun getAuthors(): Flow<NetworkResource<List<Author>>> = flow {
        emit(NetworkResource.Loading)
        val dbFlow = authorDao.getAllAuthors()

        dbFlow.collect { authors ->
            if (authors.isNotEmpty()) {
                emit(NetworkResource.Success(authors))
            }
            val lastUpdate = favouriteDao.getAllFavourites()
                .maxOfOrNull { it.addedAt }  //changed
            val currentTime = System.currentTimeMillis()
            val isStale = lastUpdate == null || (currentTime - lastUpdate) > CACHE_DURATION_MS //changed
            if (isStale) {
                try {
                    val response: AuthorsResponse = client.get("author").body()
                    val favourites = favouriteDao.getAllFavourites()
                    val favouriteNames = favourites.map { it.name }.toSet()
                    val authorEntities = response.authors.map { name ->
                        Author(name, isFavourite = favouriteNames.contains(name))
                    }
                    authorDao.clearAuthors()
                    authorDao.upsertAuthors(authorEntities)
                } catch (e: Exception) {
                    emit(NetworkResource.Fail(e.parseNetworkError()))
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getTitlesByAuthor(authorName: String): NetworkResource<List<String>> {
        return try {
            withContext(Dispatchers.IO) {
                val response: List<PoemTitleResponse> = client.get("author/$authorName/title").body()
                val titles = response.map { it.title }
                NetworkResource.Success(titles)
            }
        } catch (e: Exception) {
            Log.i("error", e.parseNetworkError())
            NetworkResource.Fail(e.parseNetworkError())
        }
    }

    override suspend fun getPoem(authorName: String, title: String): NetworkResource<Poem> {
        return try {
            withContext(Dispatchers.IO) {
                val response: List<PoemResponse> = client.get("author,title/$authorName;$title").body()
                val poemResponse = response.firstOrNull()
                if (poemResponse != null) {
                    NetworkResource.Success(
                        Poem(
                            poemResponse.title,
                            poemResponse.author,
                            poemResponse.lines,
                            poemResponse.linecount
                        )
                    )
                } else {
                    NetworkResource.Fail("Poem not found")
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