package com.example.verbumteste

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PesquisaFragment : Fragment() {

    private lateinit var searchBar: android.widget.EditText
    private lateinit var recyclerRecentes: RecyclerView
    private lateinit var recyclerResultados: RecyclerView
    private lateinit var secaoRecentes: LinearLayout
    private lateinit var secaoResultados: LinearLayout
    private lateinit var estadoVazio: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val PREFS_NAME = "verbum_prefs"
    private val KEY_RECENTES = "buscas_recentes"
    private val MAX_RECENTES = 10

    private var debounceRunnable: Runnable? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())


    inner class RecentesAdapter(
        private val items: MutableList<Pair<String, String?>>,
        private val onClick: (String) -> Unit,
        private val onRemove: (Int) -> Unit
    ) : RecyclerView.Adapter<RecentesAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val imgCapa: ImageView = view.findViewById(R.id.imgCapaRecente)
            val txtTitulo: TextView = view.findViewById(R.id.txtTituloRecente)
            val btnRemover: ImageButton = view.findViewById(R.id.btnRemoverRecente)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_busca_recente, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val (titulo, capaUrl) = items[position]
            holder.txtTitulo.text = titulo

            if (!capaUrl.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(holder.imgCapa.context)
                    .load(capaUrl)
                    .placeholder(R.drawable.brancofundo)
                    .into(holder.imgCapa)
            } else {
                holder.imgCapa.setImageResource(R.drawable.brancofundo)
            }

            holder.itemView.setOnClickListener { onClick(titulo) }
            holder.btnRemover.setOnClickListener { onRemove(holder.adapterPosition) }
        }
    }


    inner class ResultadosAdapter(
        private val items: MutableList<Livro>,
        private val onClick: (Livro) -> Unit
    ) : RecyclerView.Adapter<ResultadosAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val imgCapa: ImageView = view.findViewById(R.id.imgCapaRecente)
            val txtTitulo: TextView = view.findViewById(R.id.txtTituloRecente)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_busca_recente, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val livro = items[position]
            holder.txtTitulo.text = livro.titulo

            if (livro.capa.isNotEmpty()) {
                com.bumptech.glide.Glide.with(holder.imgCapa.context)
                    .load(livro.capa)
                    .placeholder(R.drawable.brancofundo)
                    .into(holder.imgCapa)
            } else {
                holder.imgCapa.setImageResource(R.drawable.brancofundo)
            }

            holder.itemView.findViewById<ImageButton>(R.id.btnRemoverRecente).visibility = View.GONE
            holder.itemView.setOnClickListener { onClick(livro) }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pesquisa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBar          = view.findViewById(R.id.searchBarPesquisa)
        recyclerRecentes   = view.findViewById(R.id.recyclerRecentes)
        recyclerResultados = view.findViewById(R.id.recyclerResultados)
        secaoRecentes      = view.findViewById(R.id.secaoRecentes)
        secaoResultados    = view.findViewById(R.id.secaoResultados)
        estadoVazio        = view.findViewById(R.id.estadoVazio)

        recyclerRecentes.layoutManager   = LinearLayoutManager(requireContext())
        recyclerResultados.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            findNavController().navigateUp()
        }

        carregarRecentes()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let { handler.removeCallbacks(it) }
                val query = s?.toString()?.trim() ?: ""
                if (query.isEmpty()) {
                    mostrarRecentes()
                    return
                }
                debounceRunnable = Runnable { buscarNoFirestore(query) }
                handler.postDelayed(debounceRunnable!!, 400)
            }
        })

        searchBar.setOnEditorActionListener { _, _, _ ->
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) buscarNoFirestore(query)
            true
        }
    }


    private fun carregarRecentes() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet(KEY_RECENTES, emptySet()) ?: emptySet()

        val lista = raw.map { entry ->
            val partes = entry.split("||")
            Pair(partes.getOrElse(0) { "" }, partes.getOrNull(1))
        }.filter { it.first.isNotEmpty() }.toMutableList()

        if (lista.isEmpty()) {
            mostrarRecentes()
            return
        }

        val adapter = RecentesAdapter(
            items = lista,
            onClick = { titulo ->
                searchBar.setText(titulo)
                searchBar.setSelection(titulo.length)
                buscarNoFirestore(titulo)
            },
            onRemove = { pos ->
                lista.removeAt(pos)
                recyclerRecentes.adapter?.notifyItemRemoved(pos)
                salvarRecentes(lista)
            }
        )
        recyclerRecentes.adapter = adapter
        mostrarRecentes()
    }

    private fun salvarRecente(titulo: String, capaUrl: String?) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet(KEY_RECENTES, mutableSetOf())!!.toMutableSet()
        val entrada = "$titulo||${capaUrl ?: ""}"
        raw.removeIf { it.startsWith("$titulo||") }
        raw.add(entrada)
        val limitado = raw.toList().takeLast(MAX_RECENTES).toSet()
        prefs.edit().putStringSet(KEY_RECENTES, limitado).apply()
    }

    private fun salvarRecentes(lista: List<Pair<String, String?>>) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = lista.map { "${it.first}||${it.second ?: ""}" }.toSet()
        prefs.edit().putStringSet(KEY_RECENTES, set).apply()
    }


    private fun buscarNoFirestore(query: String) {

        val variantes = buildSet {
            add(query)
            add(query.lowercase())
            add(query.replaceFirstChar { it.uppercase() })
        }.toList()

        val resultadosMesclados = mutableMapOf<String, Livro>()
        var consultasPendentes = variantes.size

        fun onConsultaConcluida() {
            consultasPendentes--
            if (consultasPendentes > 0) return

            val lista = resultadosMesclados.values
                .sortedBy { it.titulo.lowercase() }
                .toMutableList()
            if (!isAdded) return

            if (lista.isEmpty()) {
                mostrarVazio()
            } else {
                salvarRecente(query, lista.firstOrNull()?.capa)
                mostrarResultados(lista)
            }
        }

        for (variante in variantes) {
            val fim = variante + "\uf8ff"
            db.collection("obras")
                .whereGreaterThanOrEqualTo("titulo", variante)
                .whereLessThanOrEqualTo("titulo", fim)
                .limit(20)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val livro = doc.toObject(Livro::class.java)?.copy(id = doc.id)
                        if (livro != null) resultadosMesclados[doc.id] = livro
                    }
                    onConsultaConcluida()
                }
                .addOnFailureListener {
                    onConsultaConcluida()
                }
        }
    }


    private fun mostrarRecentes() {
        secaoRecentes.visibility   = View.VISIBLE
        secaoResultados.visibility = View.GONE
        estadoVazio.visibility     = View.GONE
    }

    private fun mostrarResultados(resultados: MutableList<Livro>) {
        secaoRecentes.visibility   = View.GONE
        secaoResultados.visibility = View.VISIBLE
        estadoVazio.visibility     = View.GONE

        recyclerResultados.adapter = ResultadosAdapter(
            items = resultados,
            onClick = { livro ->
                val bundle = Bundle().apply {
                    putSerializable("CHAVE_LIVRO", livro)
                }
                findNavController().navigate(
                    R.id.action_pesquisaFragment_to_detalhesLivroFragment,
                    bundle
                )
            }
        )
    }

    private fun mostrarVazio() {
        secaoRecentes.visibility   = View.GONE
        secaoResultados.visibility = View.GONE
        estadoVazio.visibility     = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debounceRunnable?.let { handler.removeCallbacks(it) }
    }
}