package com.example.musicplayer

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var listener: OnActivityListener? = null
    private lateinit var navController: NavController
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController =  Navigation.findNavController(this, R.id.my_nav_host_fragment)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onResume() {
        navController.addOnDestinationChangedListener(this)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
        listener?.onDestroyed()
    }

    fun setActivityDestroyListener(listener: OnActivityListener) {
        this.listener = listener
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (arguments!=null){
            listener?.onDestroyed()
        }
    }

    interface OnActivityListener {
        fun onDestroyed()
    }
}