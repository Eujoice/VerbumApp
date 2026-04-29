package com.example.verbumteste

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.verbumteste.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

abstract class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener { // Classe para tornar a BottomNav navegável
//abstract pra tirar o erro
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Esconde a status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())

//        binding.bottom_nav_bar.setOnItemSelectedListener { item ->
//            when(item.itemId) {
//                // parei aqui, tentando fazer a bottom_nav_bar funcionar
//            }
//        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNav.visibility = View.GONE // esconde até o login

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Conecta a BottomNav ao NavController automaticamente
        bottomNav.setupWithNavController(navController)

        // Mostra/esconde a BottomNav conforme o destino atual
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment -> {
                    bottomNav.visibility = View.GONE
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}