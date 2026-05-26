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
import com.example.verbumteste.R

class DetalhesLivroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalhes_livros, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
}