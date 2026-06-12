package com.example.verbumteste

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.verbumteste.databinding.FragmentAlterarDadosPessoaisBinding

class AlterarDadosPessoaisFragment : Fragment() {

    private var _binding: FragmentAlterarDadosPessoaisBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlterarDadosPessoaisBinding.inflate(inflater, container, false)
        return binding.root
    }


}