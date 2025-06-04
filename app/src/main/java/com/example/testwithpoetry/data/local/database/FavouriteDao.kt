package com.example.testwithpoetry.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(author: FavouriteAuthor)

    @Delete
    suspend fun delete(author: FavouriteAuthor)

    @Query("SELECT * FROM favourite_authors ORDER BY added_at DESC")
    suspend fun getAllFavourites(): List<FavouriteAuthor>

    @Query("SELECT EXISTS(SELECT * FROM favourite_authors WHERE author_name = :name)")
    suspend fun isFavourite(name: String): Boolean

    @Query("SELECT * FROM favourite_authors WHERE added_at > :timestamp")
    suspend fun getFavouritesAfter(timestamp: Long): List<FavouriteAuthor>

}