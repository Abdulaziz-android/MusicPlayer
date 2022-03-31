package com.example.musicplayer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.models.Music

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Music>)

    @Query("select * from music")
    fun getAllSongs():List<Music>?

}