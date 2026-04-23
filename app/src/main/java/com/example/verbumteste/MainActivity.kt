package com.example.verbumteste

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.verbumteste.ui.DadosPessoaisFragment
import com.example.verbumteste.ui.MinhaBibliotecaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        replaceFragment(FragmentAcervo())

        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.acervo -> {
                    replaceFragment(FragmentAcervo())
                }

                R.id.meuacervo -> {
                    replaceFragment(MinhaBibliotecaFragment())
                }

                R.id.perfil -> {
                    replaceFragment(DadosPessoaisFragment())
                }
            }
            return@setOnItemSelectedListener  true

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}