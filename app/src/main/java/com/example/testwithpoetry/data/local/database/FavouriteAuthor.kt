package com.example.testwithpoetry.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_authors")
data class FavouriteAuthor(
    @PrimaryKey
    @ColumnInfo(name = "author_name")
    val name: String,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
