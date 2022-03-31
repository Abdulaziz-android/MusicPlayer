package com.example.musicplayer.fragment

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.musicplayer.R
import com.example.musicplayer.adapter.MusicAdapter
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.db.AppDatabase
import com.example.musicplayer.models.Music
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        database = AppDatabase.getInstance(binding.root.context)
        checkPermissions()

        return binding.root
    }

    private fun getMusicFiles(file: File): ArrayList<Music> {
        val arrayFile = ArrayList<Music>()
        val allFiles = file.listFiles()
        for (indFile in allFiles) {
            if (indFile.isDirectory && !indFile.isHidden) {
                arrayFile.addAll(getMusicFiles(indFile))
            } else {
                if (indFile.name.endsWith(".mp3")) {
                    arrayFile.add(Music(musicPath = indFile.path, musicName = indFile.name))
                }
            }
        }
        return arrayFile
    }


    private fun checkPermissions() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    loadData()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}
                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            })
            .check()

    }

    private fun loadData() {
        val allSongs = database.playlistDao().getAllSongs()
        val listFile: ArrayList<Music>
        if (allSongs == null || allSongs.isEmpty()) {
            listFile = getMusicFiles(Environment.getExternalStorageDirectory())
            database.playlistDao().insert(listFile)
        } else listFile = allSongs as ArrayList<Music>
        musicAdapter = MusicAdapter(
            listFile,
            object : MusicAdapter.OnItemClickListener {
                override fun onItemClicked(
                    position: Int,
                    musicList: ArrayList<Music>
                ) {
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    bundle.putSerializable("list", musicList)
                    findNavController().navigate(
                        R.id.action_homeFragment_to_playerFragment,
                        bundle
                    )
                }
            })
        binding.rv.adapter = musicAdapter
    }


}