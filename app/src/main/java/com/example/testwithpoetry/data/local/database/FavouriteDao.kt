package com.example.testwithpoetry.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(author: FavouriteAuthor)

    @Delete
    suspend fun delete(author: FavouriteAuthor)

    @Query("SELECT * FROM favourite_authors ORDER BY added_at DESC")
    fun getAllFavourites(): Flow<List<FavouriteAuthor>>

    @Query("SELECT EXISTS(SELECT * FROM favourite_authors WHERE author_name = :name)")
    suspend fun isFavourite(name: String): Boolean

}