package com.lokavo.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lokavo.R
import com.lokavo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_maps -> binding.navView.menu.findItem(R.id.navigation_maps).isChecked =
                    true

                R.id.navigation_history -> binding.navView.menu.findItem(R.id.navigation_history).isChecked =
                    true

                R.id.navigation_history -> binding.navView.menu.findItem(R.id.navigation_profile).isChecked =
                    true

                R.id.profileDetailFragment -> binding.navView.menu.findItem(R.id.navigation_profile).isChecked =
                    true

                R.id.changePasswordFragment -> binding.navView.menu.findItem(R.id.navigation_profile).isChecked =
                    true
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}