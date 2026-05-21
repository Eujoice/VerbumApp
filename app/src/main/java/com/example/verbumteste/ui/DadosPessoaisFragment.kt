package com.example.verbumteste.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentDadosPessoaisBinding
import com.google.firebase.auth.FirebaseAuth


class DadosPessoaisFragment : Fragment() {

    private var _binding: FragmentDadosPessoaisBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDadosPessoaisBinding.inflate(inflater, container, false)
        return binding.root // ta dando erro, mas logo ele vai parar de show
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth
    }


}