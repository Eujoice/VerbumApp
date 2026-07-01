package com.example.verbumteste

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

        val formatador = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

        if (item.data_retirada != null) {
            val dataRetiradaData = item.data_retirada.toDate()
            holder.binding.txtDataEmprestimo.text = "${formatador.format(dataRetiradaData)}"
        } else {
            holder.binding.txtDataEmprestimo.text = "--/--/----"
        }

        if (item.data_devolucao_real != null) {
            val dataDevolucaoData = item.data_devolucao_real.toDate()
            holder.binding.txtDataDevolucao.text = "${formatador.format(dataDevolucaoData)}"
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