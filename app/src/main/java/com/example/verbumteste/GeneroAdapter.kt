package com.example.verbumteste

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.databinding.ItemGeneroBinding

class GeneroAdapter (
    private val secoes: List<GeneroSecao>,
    private val onItemClick: (Livro) -> Unit
) : RecyclerView.Adapter<GeneroAdapter.GeneroViewHolder>() {

    inner class GeneroViewHolder(val binding: ItemGeneroBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneroViewHolder {
        val binding = ItemGeneroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GeneroViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: GeneroViewHolder,
        position: Int
    ) {
        val secao = secoes[position]

        holder.binding.txtNomeGenero.text = secao.genero

        val livroAdapter = LivroAdapter(secao.livros, onItemClick)

        holder.binding.recyclerLivrosHorizontal.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = livroAdapter
        }
    }

    override fun getItemCount() = secoes.size
}
