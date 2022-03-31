package com.example.musicplayer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicplayer.db.dao.PlaylistDao
import com.example.musicplayer.models.Music

@Database(entities = [Music::class], version = 1)
abstract class AppDatabase :RoomDatabase(){

    abstract fun playlistDao():PlaylistDao

    companion object{
        fun getInstance(context: Context):AppDatabase{
            return Room.databaseBuilder(context,AppDatabase::class.java, "playlist_db")
                .allowMainThreadQueries()
                .build()
        }
    }
}