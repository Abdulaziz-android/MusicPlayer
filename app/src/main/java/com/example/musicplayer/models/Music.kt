package com.example.musicplayer.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Music : Serializable{
    var musicPath: String?=null
    @PrimaryKey
    var musicName: String

    constructor(musicPath: String?, musicName: String) {
        this.musicPath = musicPath
        this.musicName = musicName
    }

}