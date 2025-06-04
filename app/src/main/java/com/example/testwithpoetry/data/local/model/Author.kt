package com.example.testwithpoetry.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class Author(
    @PrimaryKey val name: String,
    val isFavourite: Boolean = false
)
