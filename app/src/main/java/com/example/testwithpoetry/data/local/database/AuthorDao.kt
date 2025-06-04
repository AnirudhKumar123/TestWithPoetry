package com.example.testwithpoetry.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testwithpoetry.data.local.model.Author
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {
    @Query("SELECT * FROM authors")
    fun getAllAuthors(): Flow<List<Author>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAuthors(authors: List<Author>)

    @Query("DELETE FROM authors")
    suspend fun clearAuthors()
} 