package com.example.musicplayer.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey
    val id:Int = 1,

)