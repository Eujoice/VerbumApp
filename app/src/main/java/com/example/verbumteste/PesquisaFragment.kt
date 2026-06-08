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
import com.google.android.gms.tasks.Tasks
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

    // ── Adapter de Recentes ──────────────────────────────────────────────────

    inner class RecentesAdapter(
        private val items: MutableList<Pair<String, String?>>, // titulo, capaUrl
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

            // Carrega capa com Glide se tiver URL, senão usa placeholder
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

    // ── Adapter de Resultados ────────────────────────────────────────────────

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

            // Oculta o botão X nos resultados (não existe remoção aqui)
            holder.itemView.findViewById<ImageButton>(R.id.btnRemoverRecente).visibility = View.GONE

            holder.itemView.setOnClickListener { onClick(livro) }
        }
    }

    // ── Ciclo de vida ────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pesquisa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBar       = view.findViewById(R.id.searchBarPesquisa)
        recyclerRecentes    = view.findViewById(R.id.recyclerRecentes)
        recyclerResultados  = view.findViewById(R.id.recyclerResultados)
        secaoRecentes   = view.findViewById(R.id.secaoRecentes)
        secaoResultados = view.findViewById(R.id.secaoResultados)
        estadoVazio     = view.findViewById(R.id.estadoVazio)

        recyclerRecentes.layoutManager   = LinearLayoutManager(requireContext())
        recyclerResultados.layoutManager = LinearLayoutManager(requireContext())

        // Botão voltar
        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            findNavController().navigateUp()
        }

        carregarRecentes()

        // Debounce de 400ms ao digitar
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

        // Busca ao pressionar a tecla de busca no teclado
        searchBar.setOnEditorActionListener { _, _, _ ->
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) buscarNoFirestore(query)
            true
        }
    }

    // ── Recentes (SharedPreferences) ─────────────────────────────────────────

    private fun carregarRecentes() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet(KEY_RECENTES, emptySet()) ?: emptySet()

        // Formato salvo: "titulo||capaUrl"
        val lista = raw.map { entry ->
            val partes = entry.split("||")
            Pair(partes.getOrElse(0) { "" }, partes.getOrNull(1))
        }.filter { it.first.isNotEmpty() }.toMutableList()

        if (lista.isEmpty()) {
            mostrarRecentes() // mostra seção vazia mesmo assim
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
                val removido = lista[pos]
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
        raw.remove(raw.find { it.startsWith("$titulo||") }) // remove duplicata
        raw.add(entrada)
        // Limita a MAX_RECENTES
        val limitado = raw.toList().takeLast(MAX_RECENTES).toSet()
        prefs.edit().putStringSet(KEY_RECENTES, limitado).apply()
    }

    private fun salvarRecentes(lista: List<Pair<String, String?>>) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = lista.map { "${it.first}||${it.second ?: ""}" }.toSet()
        prefs.edit().putStringSet(KEY_RECENTES, set).apply()
    }

    // ── Busca no Firestore ───────────────────────────────────────────────────

    private fun buscarNoFirestore(query: String) {
        val prefixFim = query + "\uf8ff"

        db.collection("obras")
            .whereGreaterThanOrEqualTo("titulo", query)
            .whereLessThanOrEqualTo("titulo", prefixFim)
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val resultados = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Livro::class.java)?.copy(id = doc.id)
                }.toMutableList()

                if (resultados.isEmpty()) {
                    mostrarVazio()
                } else {
                    // Salva o primeiro resultado como recente
                    salvarRecente(query, resultados.firstOrNull()?.capa)
                    mostrarResultados(resultados)
                }
            }
            .addOnFailureListener {
                mostrarVazio()
            }
    }

    // ── Controle de visibilidade ─────────────────────────────────────────────

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
                // Navega para detalhes passando o ID do livro
                val bundle = Bundle().apply { putString("livroId", livro.id) }
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