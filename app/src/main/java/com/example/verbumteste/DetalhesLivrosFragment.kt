package com.example.biblioteca.ui

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

class DetalhesLivroFragment : Fragment() {

    private var _binding: FragmentDetalhesLivrosBinding? = null
    private val binding get() = _binding!!

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

        // Para poder receber os dados dos livros
        val livro = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("CHAVE_LIVRO", Livro::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("CHAVE_LIVRO") as? Livro
        }

        livro?.let {
            Glide.with(this).load(it.capa).into(binding.imgCapaLivro)
            binding.tvTituloLivro.text = it.titulo
            binding.tvAutorLivro.text = it.autor
            binding.tvDescricaoLivro.text = it.sinopse
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

        // Botão Favoritar
        val btnFavorito = view.findViewById<ImageView>(R.id.btnFavorito)
        var isFavorito = false
        btnFavorito.setOnClickListener {
            isFavorito = !isFavorito
            if (isFavorito) {
                btnFavorito.setImageResource(R.drawable.ic_favorite_border)
                Toast.makeText(requireContext(), "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
            } else {
                btnFavorito.setImageResource(R.drawable.ic_favorite_border)
                Toast.makeText(requireContext(), "Removido dos favoritos.", Toast.LENGTH_SHORT).show()
            }
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