package com.example.isyara

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.isyara.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi NavController dari NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // No back-back geming
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragmentId = navController.currentDestination?.id

                // Fragment yang di banned untuk klik back
                val fragmentsRequiringExitConfirmation = setOf(
                    R.id.homeScreenFragment,
                    R.id.onboardFragment //kalau mau add fragment lagi kasih koma terus copas aja
                )

                if (currentFragmentId in fragmentsRequiringExitConfirmation) {
                    showExitConfirmationDialog()
                } else {
                    navController.popBackStack()
                }
            }
        })
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Keluar Aplikasi")
            setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            setPositiveButton("Ya") { _, _ ->
                finish()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }
}
