package com.example.verbumteste.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import org.mindrot.jbcrypt.BCrypt

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore  // Para realizar login com o firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            //validateData()
            findNavController().navigate(R.id.action_global_to_acervoFragment)
        }
    }

    // função para realizar login
    private fun validateData() {
        val matricula = binding.editTxtMatricula.text.toString().trim()
        val senha = binding.editTxtPassword.text.toString().trim()

        if (matricula.isNotBlank()) {
            if (senha.isNotBlank()) {
                loginUser(matricula, senha)
            } else {
                Toast.makeText(requireContext(), "Preencha a senha!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Preencha a matrícula!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(matricula: String, senhaDigitada: String) {
         // Ainda não concluído
        db.collection("usuarios")
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "Matrícula não encontrada!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val document = documents.documents[0]
                val senhaBanco = document.get("senha")
                val senhaBD = senhaBanco?.toString()?.trim()

                if (!senhaBD.isNullOrEmpty()) {
                   try {
                        // O BCrypt.checkpw compara a senha em texto limpo com o hash complexo do banco
                        val senhaCorreta = BCrypt.checkpw(senhaDigitada, senhaBD)

                        if (senhaCorreta) {
                            Toast.makeText(requireContext(), "Login bem sucedido", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()

                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Achou erro", Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Toast.makeText(requireContext(), "Erro: O campo retornou nulo ou vazio. Objeto bruto: $senhaBanco", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao conectar ao banco: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}