package com.example.verbumteste

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.databinding.ItemLivroBinding
import com.bumptech.glide.Glide

class LivroAdapter(
    var livroList: List<Livro>
): RecyclerView.Adapter<LivroAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = ItemLivroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val livro = livroList[position]
        holder.binding.txtTituloLivro.text = livro.titulo
        holder.binding.txtAutorLivro.text = livro.autor

        Glide.with(holder.itemView.context)
            .load(livro.capa)
            //.placeholder(R.drawable.placeholder_livro)
            .into(holder.binding.imgCapa)
    }

    override fun getItemCount() = livroList.size

    fun atualizarLista(novaLista: List<Livro>) {
        this.livroList = novaLista
        notifyDataSetChanged() // Avisa o RecyclerView para se redesenhar com os novos dados
    }

    inner class MyViewHolder(val binding: ItemLivroBinding) : RecyclerView.ViewHolder(binding.root)
}