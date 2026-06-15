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

        db = FirebaseFirestore.getInstance()

        val prefs = requireContext().getSharedPreferences("verbum_prefs", android.content.Context.MODE_PRIVATE)
        val usuarioLogado = prefs.getString("id_usuario_logado", "") ?: ""

        if (usuarioLogado.isNotEmpty()) {
            // Se o ID existe na memória, vai para o Acervo
            findNavController().navigate(R.id.action_global_to_acervoFragment)
            return // Para o código e não carrega o clique do botão
        }

        binding.btnLogin.setOnClickListener {
            validateData()
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
                       // Método para corrigir o início do hash
                       val hash = if (senhaBD.startsWith("$2y$")) {
                           senhaBD.replaceFirst("$2y$", "$2a$") // Caso o início seja "$2y$", muda para "$2a$"
                       } else {
                           senhaBD
                       }
                        // O BCrypt.checkpw compara a senha em texto limpo com o hash complexo do banco
                        val senhaCorreta = BCrypt.checkpw(senhaDigitada, hash)

                        if (senhaCorreta) {
                            val idUsuario = document.id
                            val prefs = requireContext().getSharedPreferences("verbum_prefs", android.content.Context.MODE_PRIVATE)
                            prefs.edit().putString("id_usuario_logado", idUsuario).apply()
                            findNavController().navigate(R.id.action_global_to_acervoFragment)
                        } else {
                            Toast.makeText(requireContext(), "Login ou senha incorretos!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Erro na criptografia: ${e.message}", Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Toast.makeText(requireContext(), "Erro: O campo retornou nulo ou vazio", Toast.LENGTH_SHORT).show()
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