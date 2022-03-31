package com.example.musicplayer.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.models.Music

class PlayerFragment : Fragment(), MainActivity.OnActivityListener {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler
    private var musicFiles: ArrayList<Music>? = null
    private var position: Int = 0
    private var isForegroundPlaying = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(layoutInflater, container, false)
        handler = Handler(Looper.getMainLooper())
        musicFiles = arguments?.getSerializable("list") as ArrayList<Music>
        position = arguments?.getInt("position")!!
        if (mediaPlayer == null) {
            playMusic(position)
            setOnClickListeners()
        }


        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListeners() {
        binding.imageView.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    mediaPlayer!!.pause()
                }

                MotionEvent.ACTION_UP -> {
                    mediaPlayer!!.start()
                }
            }

            true
        }

        binding.playCard.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer!!.start()
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
            }
        }

        binding.nextBtn.setOnClickListener { if (position + 1 < musicFiles!!.size) playMusic(++position) }
        binding.previousBtn.setOnClickListener { if (position > 0) playMusic(--position) }

        binding.replayBtn.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.minus(30000)!!)
        }
        binding.forwardBtn.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(30000)!!)
        }


        binding.seekBarTime.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private var runnable = object : Runnable {
        override fun run() {
            binding.seekBarTime.progress = mediaPlayer!!.currentPosition
            binding.discCard.rotation = (mediaPlayer!!.currentPosition / 10).toFloat()
            handler.postDelayed(this, 10)
            val buf = StringBuffer()

            buf
                .append(String.format("%02d", (mediaPlayer!!.currentPosition / (1000 * 60 * 60))))
                .append(":")
                .append(
                    String.format(
                        "%02d",
                        (mediaPlayer!!.currentPosition % (1000 * 60 * 60) / (1000 * 60))
                    )
                )
                .append(":")
                .append(
                    String.format(
                        "%02d",
                        (mediaPlayer!!.currentPosition % (1000 * 60 * 60) % (1000 * 60) / 1000)
                    )
                )


            binding.tvTime.text = buf.toString()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun playMusic(pos: Int) {
        releaseMp()

        val musicPath = musicFiles?.get(pos)!!.musicPath
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(musicPath)
        val data = mmr.embeddedPicture
        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.imageView.setImageBitmap(bitmap)
            binding.imageFront.setImageBitmap(bitmap)
        } else {
            binding.imageView.setImageResource(R.drawable.icon_music_player)
            binding.imageFront.setImageResource(R.drawable.icon_music_player)
        }
        //----------------------------------------
        var singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        var name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        if (singer == null) singer = "Unknown"
        if (name == null) name = "Unknown"
        binding.songName.text = name
        binding.artistNameTv.text = singer


        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(musicPath)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
        val duration = mediaPlayer?.duration!!
        val buf = StringBuffer()

        buf.append(String.format("%02d", (duration / (1000 * 60 * 60))))
            .append(":")
            .append(String.format("%02d", (duration % (1000 * 60 * 60) / (1000 * 60))))
            .append(":")
            .append(String.format("%02d", (duration % (1000 * 60 * 60) % (1000 * 60) / 1000)))


        binding.tvDuration.text = buf.toString()

        mediaPlayer!!.setOnCompletionListener {
            playMusic(++position)
        }

        binding.period.text = "${position + 1}/${musicFiles?.size}"
        binding.btnPlay.setImageResource(R.drawable.ic_pause)
        binding.seekBarTime.max = mediaPlayer!!.duration
        handler.postDelayed(runnable, 10)

        (activity as MainActivity).setActivityDestroyListener(this@PlayerFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        isForegroundPlaying = false
    }

    private fun releaseMp() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyed() {
        if (!isForegroundPlaying) {
            releaseMp()
            handler.removeCallbacksAndMessages(null)
        }
    }

}