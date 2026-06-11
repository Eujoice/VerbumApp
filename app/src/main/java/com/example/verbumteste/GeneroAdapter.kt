package com.example.verbumteste

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.databinding.ItemGeneroBinding
import com.example.verbumteste.databinding.ItemLivroBinding

class GeneroAdapter (
    private val secoes: List<GeneroSecao>
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

        val livroAdapter = LivroAdapter(secao.livros)

        holder.binding.recyclerLivrosHorizontal.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = livroAdapter
        }
    }

    override fun getItemCount() = secoes.size
}
