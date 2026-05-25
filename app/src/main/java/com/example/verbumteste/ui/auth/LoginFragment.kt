package com.example.verbumteste.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)?.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_loginFragment_to_fragment_acervo)
        }

        auth = FirebaseAuth.getInstance()
    }

    // função para realizar login
    private fun validateData() {
        //val email = binding.editTxtEmail.text.toString().trim()
        // ainda nao terminei

    }
}