package com.example.testwithpoetry.di

import android.content.Context
import com.example.testwithpoetry.data.local.database.AppDatabase
import com.example.testwithpoetry.data.local.database.FavouriteDao
import com.example.testwithpoetry.data.repository.PoetryRepositoryImpl
import com.example.testwithpoetry.domain.repository.PoetryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun providesFavouriteDao(db: AppDatabase): FavouriteDao {
        return db.favouriteDao()
    }

    @Provides
    @Singleton
    fun providesHttClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                url("https://poetrydb.org/")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    @Provides
    @Singleton
    fun providePoetryRepository(
        client: HttpClient,
        favouriteDao: FavouriteDao
    ): PoetryRepository {
        return PoetryRepositoryImpl(client, favouriteDao)
    }
}