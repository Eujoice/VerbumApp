package com.example.verbumteste

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class AlterarSenhaFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etSenhaAtual: EditText
    private lateinit var etNovaSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnVoltar: ImageView
    private lateinit var tvForcaSenha: TextView
    private lateinit var seg1: View
    private lateinit var seg2: View
    private lateinit var seg3: View
    private lateinit var seg4: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alterar_senha, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        etSenhaAtual = view.findViewById(R.id.etSenhaAtual)
        etNovaSenha = view.findViewById(R.id.etNovaSenha)
        etConfirmarSenha = view.findViewById(R.id.etConfirmarSenha)
        btnSalvar = view.findViewById(R.id.btnSalvar)
        btnVoltar = view.findViewById(R.id.btnVoltar)
        tvForcaSenha = view.findViewById(R.id.tvForcaSenha)
        seg1 = view.findViewById(R.id.seg1)
        seg2 = view.findViewById(R.id.seg2)
        seg3 = view.findViewById(R.id.seg3)
        seg4 = view.findViewById(R.id.seg4)

        btnVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSalvar.setOnClickListener {
            alterarSenha()
        }

        etNovaSenha.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                atualizarForcaSenha(s.toString())
            }
        })
    }

    private fun atualizarForcaSenha(senha: String) {
        val fraca = Color.parseColor("#e0e0e0")
        val media = Color.parseColor("#f0a840")
        val forte = Color.parseColor("#4a8c43")

        when {
            senha.isEmpty() -> {
                listOf(seg1, seg2, seg3, seg4).forEach { it.setBackgroundColor(fraca) }
                tvForcaSenha.text = ""
            }
            senha.length < 4 -> {
                seg1.setBackgroundColor(Color.parseColor("#e24b4a"))
                listOf(seg2, seg3, seg4).forEach { it.setBackgroundColor(fraca) }
                tvForcaSenha.text = "Senha muito fraca"
                tvForcaSenha.setTextColor(Color.parseColor("#e24b4a"))
            }
            senha.length < 6 -> {
                listOf(seg1, seg2).forEach { it.setBackgroundColor(media) }
                listOf(seg3, seg4).forEach { it.setBackgroundColor(fraca) }
                tvForcaSenha.text = "Senha fraca"
                tvForcaSenha.setTextColor(media)
            }
            senha.length < 10 || !senha.any { it.isDigit() } -> {
                listOf(seg1, seg2, seg3).forEach { it.setBackgroundColor(forte) }
                seg4.setBackgroundColor(fraca)
                tvForcaSenha.text = "Senha boa"
                tvForcaSenha.setTextColor(forte)
            }
            else -> {
                listOf(seg1, seg2, seg3, seg4).forEach { it.setBackgroundColor(forte) }
                tvForcaSenha.text = "Senha forte"
                tvForcaSenha.setTextColor(forte)
            }
        }
    }

    private fun alterarSenha() {
        val senhaAtual = etSenhaAtual.text.toString().trim()
        val novaSenha = etNovaSenha.text.toString().trim()
        val confirmarSenha = etConfirmarSenha.text.toString().trim()

        if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }
        if (novaSenha.length < 6) {
            Toast.makeText(requireContext(), "A nova senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return
        }
        if (novaSenha != confirmarSenha) {
            Toast.makeText(requireContext(), "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = auth.currentUser
        if (usuario == null || usuario.email == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        btnSalvar.isEnabled = false

        val credencial = EmailAuthProvider.getCredential(usuario.email!!, senhaAtual)
        usuario.reauthenticate(credencial)
            .addOnSuccessListener {
                usuario.updatePassword(novaSenha)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                        btnSalvar.isEnabled = true
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erro ao alterar senha: ${e.message}", Toast.LENGTH_LONG).show()
                        btnSalvar.isEnabled = true
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Senha atual incorreta.", Toast.LENGTH_SHORT).show()
                btnSalvar.isEnabled = true
            }
    }
}