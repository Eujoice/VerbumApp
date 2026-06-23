package com.example.verbumteste

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.verbumteste.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-edge ANTES do setContentView
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. ViewBinding — apenas UMA chamada ao setContentView
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Esconde a Status Bar (modo imersivo)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 4. Inset da BottomNav para não ser cortada pela navigation bar do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavBar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
// ta um pequeno erro, a parte de cima tá branca
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->

            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val cutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())


            val topPadding = maxOf(statusBarInsets.top, cutoutInsets.top)


            view.setPadding(view.paddingLeft, topPadding, view.paddingRight, view.paddingBottom)

            insets
        }

        // 5. Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 6. Conecta BottomNav ao NavController
        binding.bottomNavBar.setupWithNavController(navController)

        // 7. Controle de visibilidade da BottomNav por destino
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment -> {
                    binding.bottomNavBar.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavBar.visibility = View.VISIBLE
                }
            }
        }
    }
}