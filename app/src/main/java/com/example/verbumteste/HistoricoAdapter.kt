package com.example.verbumteste

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.verbumteste.databinding.ItemHistoricoEmprestimoBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoricoAdapter(
    private var listaHistorico: List<Historico>
) : RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoricoViewHolder {
        val binding = ItemHistoricoEmprestimoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoricoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val item = listaHistorico[position]

        holder.binding.txtTituloHistorico.text = item.titulo_obra

        Glide.with(holder.itemView.context)
            .load(item.capa_obra)
            //.placeholder(R.drawable.placeholder_livro)
            .into(holder.binding.imgCapaHistorico)

        if (!item.data_retirada.isNullOrEmpty()) {
            holder.binding.txtDataEmprestimo.text = item.data_retirada
        } else {
            holder.binding.txtDataEmprestimo.text = "--/--/----"
        }

        if (!item.data_devolucao_real.isNullOrEmpty()) {
            holder.binding.txtDataDevolucao.text = item.data_devolucao_real
        } else {
            // Caso esteja com o suário e não tenha sido devolvido
            holder.binding.txtDataDevolucao.text = "Em andamento"
        }
    }

    override fun getItemCount() = listaHistorico.size

    fun atualizarLista(novaLista: List<Historico>) {
        this.listaHistorico = novaLista
        notifyDataSetChanged()
    }

    inner class HistoricoViewHolder(val binding: ItemHistoricoEmprestimoBinding) : RecyclerView.ViewHolder(binding.root)
}