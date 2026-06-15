package com.example.verbumteste.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentDadosPessoaisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DadosPessoaisFragment : Fragment() {

    private var _binding: FragmentDadosPessoaisBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore // Logout com firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // removi o '?' aqui porque o View Binding garante que a View não será nula
        _binding = FragmentDadosPessoaisBinding.inflate(inflater, container, false)
        return binding.root // agora sim, sem erro e parou de show
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        binding.btnDadosPessoais.setOnClickListener {
            findNavController().navigate(R.id.action_dadosPessoaisFragment_to_alterarDadosPessoaisFragment)
        }

        binding.btnFavoritos.setOnClickListener {
            findNavController().navigate(R.id.action_dadosPessoaisFragment_to_favoritosFragment)
        }

        binding.btnHistorico.setOnClickListener {
            findNavController().navigate(R.id.action_dadosPessoaisFragment_to_historicoEmprestimoFragment)
        }
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            builder.setTitle("Sair")
            builder.setMessage("Tem certeza que deseja sair?")
            builder.setPositiveButton("Sim") { dialog, _ ->
                val prefs = requireContext().getSharedPreferences("verbum_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().remove("id_usuario_logado").apply() // Logout
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