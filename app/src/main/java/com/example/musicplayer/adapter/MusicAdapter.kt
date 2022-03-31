package com.example.musicplayer.adapter

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.databinding.MusicItemBinding
import com.example.musicplayer.models.Music

class MusicAdapter(val musicList: ArrayList<Music>, val listener: OnItemClickListener) : RecyclerView.Adapter<MusicAdapter.MusicVH>() {

    inner class MusicVH(private val itemBinding: MusicItemBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun onBind(music: Music, position: Int){
            try {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(music.musicPath)
                val data = mmr.embeddedPicture
                if (data != null) {
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    itemBinding.imageView.setImageBitmap(bitmap)
                } else{
                    itemBinding.imageView.setImageResource(R.drawable.icon_music_player)
                }
                var singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                var name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                if (singer==null) singer = music.musicName
                if (name==null) name = "Unknown"
                itemBinding.songName.text = name
                itemBinding.authorName.text = singer

                itemBinding.root.setOnClickListener {
                    listener.onItemClicked(position, musicList)
                }
            }catch (e:Exception){
                Toast.makeText(itemBinding.root.context, e.message?:"Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicVH {
        return MusicVH(MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MusicVH, position: Int) {
        holder.onBind(musicList[position], position)
    }

    override fun getItemCount(): Int = musicList.size

    interface OnItemClickListener{
        fun onItemClicked(position: Int, musicList: ArrayList<Music>)
    }
}