package com.example.verbumteste

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.verbumteste.databinding.ActivityMainBinding
import com.example.verbumteste.databinding.FragmentMinhaBibliotecaBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//o main activity estava com muitos erros, pedi pro gemini tentar ajeitar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuração do ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Esconde a Status Bar (Modo imersivo)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 3. Configuração do Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 4. Conecta a BottomNav ao NavController
        // Importante: Os IDs no seu menu (XML) devem ser IGUAIS aos IDs no seu nav_graph.xml
        binding.bottomNavBar.setupWithNavController(navController)


        // 5. Controle de visibilidade da BottomNav
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