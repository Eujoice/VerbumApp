package com.example.biblioteca.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.verbumteste.Livro
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentDetalhesLivrosBinding
import com.google.firebase.firestore.FirebaseFirestore


class DetalhesLivroFragment : Fragment() {

    private var _binding: FragmentDetalhesLivrosBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private var idDocumentoFavorito: String? = null
    private var idUsuarioLogado: String = ""
    private var isFavorito = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesLivrosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Captura o ID do usuário logado
        val prefs = requireContext().getSharedPreferences("verbum_prefs", Context.MODE_PRIVATE)
        idUsuarioLogado = prefs.getString("id_usuario_logado", "") ?: ""

        if (idUsuarioLogado.isEmpty()) {
            Toast.makeText(requireContext(), R.string.realize_login, Toast.LENGTH_SHORT).show()
        }

        val btnFavorito = view.findViewById<ImageView>(R.id.btnFavorito)

        // Para poder receber os dados dos livros
        val livro = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("CHAVE_LIVRO", Livro::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("CHAVE_LIVRO") as? Livro
        }

        livro?.let { currentLivro ->
            Glide.with(this).load(currentLivro.capa).into(binding.imgCapaLivro)
            binding.tvTituloLivro.text = currentLivro.titulo
            binding.tvAutorLivro.text = currentLivro.autor
            binding.tvDescricaoLivro.text = currentLivro.sinopse
            binding.chipDisponibilidade.text = currentLivro.status

            if (idUsuarioLogado.isNotEmpty()) {
                // Busca se este livro já foi favoritado por este usuário
                db.collection("favoritos")
                    .whereEqualTo("usuario_id", idUsuarioLogado)
                    .whereEqualTo("obra_id", currentLivro.id)
                    .get()
                    .addOnSuccessListener { snapshots ->
                        if (!snapshots.isEmpty) {
                            isFavorito = true
                            idDocumentoFavorito = snapshots.documents.first().id
                            btnFavorito.setImageResource(R.drawable.group49)
                        } else {
                            isFavorito = false
                            idDocumentoFavorito = null
                            btnFavorito.setImageResource(R.drawable.component4)
                        }
                    }
            }

            btnFavorito.setOnClickListener {
                if (idUsuarioLogado.isEmpty()) {
                    // Se o user não estiver logado, pede-se para que ele logue para poder favoritar
                    Toast.makeText(requireContext(), R.string.realize_login, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (isFavorito && idDocumentoFavorito != null) {
                    // Remove documento usando o id salvo
                    db.collection("favoritos").document(idDocumentoFavorito!!)
                        .delete()
                        .addOnSuccessListener {
                            isFavorito = false
                            idDocumentoFavorito = null
                            btnFavorito.setImageResource(R.drawable.component4)
                            Toast.makeText(requireContext(), R.string.remove_favoritos, Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Erro ao remover: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Cria documento com os dados requeridos para favoritar
                    val dadosFavorito = hashMapOf(
                        "obra_id" to currentLivro.id,
                        "usuario_id" to idUsuarioLogado,
                        "salvo_em" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    db.collection("favoritos")
                        .add(dadosFavorito)
                        .addOnSuccessListener { documentReference ->
                            isFavorito = true
                            idDocumentoFavorito = documentReference.id
                            btnFavorito.setImageResource(R.drawable.group49)
                            Toast.makeText(requireContext(), R.string.add_aos_favoritos, Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Erro ao favoritar: ${e.message}", Toast.LENGTH_SHORT).show()
                            android.util.Log.e("ERRO_FAVORITOS", "Falha ao enviar:", e)
                        }
                }
            }
        }



        val tvDescricao = view.findViewById<TextView>(R.id.tvDescricaoLivro)
        val tvVerMais   = view.findViewById<TextView>(R.id.tvVerMais)
        var expandido   = false

        tvVerMais.setOnClickListener {
            expandido = !expandido
            if (expandido) {
                tvDescricao.maxLines = Int.MAX_VALUE
                tvVerMais.text = "Ver menos"
            } else {
                tvDescricao.maxLines = 4
                tvVerMais.text = "Ver mais"
            }
        }

        // Botão Voltar
        view.findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botão Avaliar
        view.findViewById<TextView>(R.id.btnAvaliar).setOnClickListener {
            // TODO: Abrir diálogo de avaliação com RatingBar
            Toast.makeText(requireContext(), "Avalie este livro!", Toast.LENGTH_SHORT).show()
        }

        // Botão Reservar
        view.findViewById<Button>(R.id.btnReservar).setOnClickListener {
            // TODO: Chamar API de reserva
            Toast.makeText(requireContext(), "Livro reservado com sucesso! ✅", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}