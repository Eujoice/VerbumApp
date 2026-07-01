package com.example.verbumteste

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.databinding.FragmentHistoricoEmprestimoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HistoricoEmprestimoFragment : Fragment() {

    private var _binding: FragmentHistoricoEmprestimoBinding? = null
    private val binding get() = _binding!!
    private lateinit var historicoAdapter: HistoricoAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvHistorico: RecyclerView
    private lateinit var layoutVazio: View
    private lateinit var btnVoltar: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoricoEmprestimoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("verbum_prefs", Context.MODE_PRIVATE)
        val idUsuarioLogado = prefs.getString("id_usuario_logado", "") ?: ""

        rvHistorico = view.findViewById(R.id.recyclerHistorico)
        layoutVazio = view.findViewById(R.id.layoutVazio)
        btnVoltar = view.findViewById(R.id.btnVoltar)

        historicoAdapter = HistoricoAdapter(emptyList())
        rvHistorico.layoutManager = LinearLayoutManager(requireContext())
        rvHistorico.adapter = historicoAdapter

        if (idUsuarioLogado.isNotEmpty()) {
            carregarHistorico(idUsuarioLogado)
        }

        btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun carregarHistorico(idUsuario: String) {
        db.collection("historico")
            .whereEqualTo("usuario_id", idUsuario)
            .get()
            .addOnSuccessListener { snapshots ->
                // Proteção caso o usuário saia da tela enquanto o banco responde
                if (!isAdded || _binding == null) return@addOnSuccessListener

                if (snapshots == null || snapshots.isEmpty) {

                    rvHistorico.visibility = View.GONE
                    layoutVazio.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                val listaHistorico = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(Historico::class.java)
                }

                val idsDasObras = listaHistorico.map { it.obra_id }.distinct().filter { it.isNotEmpty() }

                if (idsDasObras.isEmpty()) {
                    exibirListaOuVazio(listaHistorico)
                    return@addOnSuccessListener
                }

                db.collection("obras")
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), idsDasObras)
                    .get()
                    .addOnSuccessListener { resultadosObras ->
                        if (!isAdded || _binding == null) return@addOnSuccessListener

                        val mapCapas = resultadosObras.documents.associate { doc ->
                            doc.id to doc.getString("capa")
                        }

                        for (item in listaHistorico) {
                            item.capa_obra = mapCapas[item.obra_id]
                        }

                        exibirListaOuVazio(listaHistorico)
                    }
                    .addOnFailureListener {
                        exibirListaOuVazio(listaHistorico)
                    }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erro ao carregar histórico", Toast.LENGTH_SHORT).show()                }
            }
    }

    private fun exibirListaOuVazio(lista: List<Historico>) {
        if (lista.isNotEmpty()) {
            layoutVazio.visibility = View.GONE
            rvHistorico.visibility = View.VISIBLE
            val listaOrdenada = lista.sortedByDescending { it.data_retirada }
            historicoAdapter.atualizarLista(listaOrdenada)
        } else {
            rvHistorico.visibility = View.GONE
            layoutVazio.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}