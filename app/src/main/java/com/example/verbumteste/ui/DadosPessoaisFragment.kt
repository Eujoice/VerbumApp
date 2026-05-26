package com.example.verbumteste.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentDadosPessoaisBinding
import com.google.firebase.auth.FirebaseAuth

class DadosPessoaisFragment : Fragment() {

    private var _binding: FragmentDadosPessoaisBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // removi o '?' aqui porque o View Binding garante que a View não será nula
        _binding = FragmentDadosPessoaisBinding.inflate(inflater, container, false)
        return binding.root // agora sim, sem erro e parou de show
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // aproveitei para inicializar o Firebase corretamente, senão daria erro ao rodar
        auth = FirebaseAuth.getInstance()

        initListeners()
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            builder.setTitle("Sair")
            builder.setMessage("Tem certeza que deseja sair?")
            builder.setPositiveButton("Sim") { dialog, _ ->
                auth.signOut()
                findNavController().navigate(R.id.action_dadosPessoaisFragment_to_loginFragment)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Fecha o pop-up
            }


            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}