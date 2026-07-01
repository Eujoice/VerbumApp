import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.R
import com.example.verbumteste.Genero

class GeneroAdapter2(
    private val listaGeneros: List<Genero>,
    private val onGeneroClick: (Genero) -> Unit
) : RecyclerView.Adapter<GeneroAdapter2.GeneroViewHolder>() {

    inner class GeneroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bg: View = view.findViewById(R.id.bgGenero)
        val tvNome: TextView = view.findViewById(R.id.tvNomeGenero)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genero, parent, false)
        return GeneroViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeneroViewHolder, position: Int) {
        val genero = listaGeneros[position]
        holder.tvNome.text = genero.nome
        (holder.bg.background as GradientDrawable).setColor(Color.parseColor(genero.cor))
        holder.itemView.setOnClickListener { onGeneroClick(genero) }
    }

    override fun getItemCount(): Int = listaGeneros.size
}