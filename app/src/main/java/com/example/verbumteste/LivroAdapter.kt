package com.example.verbumteste

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.databinding.ItemLivroBinding

class LivroAdapter(
    var livroList: List<Livro>
): RecyclerView.Adapter<LivroAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class MyViewHolder(val binding: ItemLivroBinding) : RecyclerView.ViewHolder(binding.root)
}