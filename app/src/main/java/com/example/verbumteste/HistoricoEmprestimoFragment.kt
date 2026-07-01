package com.example.verbumteste

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HistoricoEmprestimoFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_historico_emprestimo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("verbum_prefs", Context.MODE_PRIVATE)
        val idUsuarioLogado = prefs.getString("id_usuario_logado", "") ?: ""

        rvHistorico = view.findViewById(R.id.rvHistoricoEmprestimos)
        layoutVazio = view.findViewById(R.id.layoutVazio)
        btnVoltar = view.findViewById(R.id.btnVoltar)

        historicoAdapter = HistoricoAdapter(emptyList())
        rvHistorico.layoutManager = LinearLayoutManager(requireContext())
        rvHistorico.adapter = historicoAdapter

        if (idUsuarioLogado.isNotEmpty()) {
            carregarHistorico() // Parei aqui
        }

        btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }

        carregarHistorico()
    }

    private fun carregarHistorico() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // ajuste o caminho conforme a estrutura do Firebase
        val ref = FirebaseDatabase.getInstance()
            .getReference("emprestimos")
            .child(uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Emprestimo>()

                for (child in snapshot.children) {
                    val emprestimo = child.getValue(Emprestimo::class.java)
                    emprestimo?.let { lista.add(it) }
                }

                lista.sortByDescending { it.dataEmprestimo }

                if (lista.isEmpty()) {
                    rvHistorico.visibility = View.GONE
                    layoutVazio.visibility = View.VISIBLE
                } else {
                    rvHistorico.visibility = View.VISIBLE
                    layoutVazio.visibility = View.GONE
                    //rvHistorico.adapter = HistoricoAdapter(lista)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // tratar erro se necessário
            }
        })
    }
}