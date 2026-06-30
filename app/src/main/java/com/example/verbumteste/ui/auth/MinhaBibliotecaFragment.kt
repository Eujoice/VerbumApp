package com.example.verbumteste.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verbumteste.Livro
import com.example.verbumteste.LivroAdapter
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentMinhaBibliotecaBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class MinhaBibliotecaFragment : Fragment() {

    private var _binding: FragmentMinhaBibliotecaBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private var idUsuarioLogado: String = ""
    private lateinit var livroAdapter: LivroAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMinhaBibliotecaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("verbum_prefs", Context.MODE_PRIVATE)
        idUsuarioLogado = prefs.getString("id_usuario_logado", "") ?: ""

        binding.recyclerReservados.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        livroAdapter = LivroAdapter(mutableListOf(), onItemClick = { livroClicado ->
            val bundle = Bundle().apply {
                putSerializable("CHAVE_LIVRO", livroClicado)
            }
            findNavController().navigate(R.id.action_minhaBibliotecaFragment_to_detalhesLivroFragment, bundle)
        })
        binding.recyclerReservados.adapter = livroAdapter

        if (idUsuarioLogado.isNotEmpty()) {
            buscarReservados(idUsuarioLogado)
        } else {
            binding.estadoVazio.visibility = View.VISIBLE
            binding.recyclerReservados.visibility = View.GONE
            Toast.makeText(requireContext(), R.string.realize_login, Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarReservados(matriculaLogada: String) {
        db.collection("reservas")
            .whereEqualTo("matricula", matriculaLogada)
            .addSnapshotListener { snapshots, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                // Para não crashar quando o botão reservar dos detalhes for clicado
                if (!isAdded || _binding == null) {
                    return@addSnapshotListener
                }

                // Se não houver livros reservados
                if (snapshots == null || snapshots.isEmpty) {
                    mostrarEstadoVazio()
                    return@addSnapshotListener
                }

                // Mapeia os campos "obra_id" da coleção reservas deste usuário
                val idsDosLivros = snapshots.documents.mapNotNull { it.getString("obra_id") }

                if (idsDosLivros.isEmpty()) {
                    mostrarEstadoVazio()
                    return@addSnapshotListener
                }

                // Busca todas as obras com os ids recuperados acima (para buscar e exibir os dados dos livros)
                db.collection("obras")
                    .whereIn(FieldPath.documentId(), idsDosLivros)
                    .get()
                    .addOnSuccessListener { resultadoLivros ->

                        // Correção anti crash ao clicar em reservar
                        if (!isAdded || _binding == null) return@addOnSuccessListener

                        val listaDeLivros = resultadoLivros.documents.mapNotNull { doc ->
                            doc.toObject(Livro::class.java)?.copy(id = doc.id)
                        }.toMutableList()

                        if (listaDeLivros.isNotEmpty()) {
                            binding.estadoVazio.visibility = View.GONE
                            binding.recyclerReservados.visibility = View.VISIBLE

                            listaDeLivros.sortBy { it.titulo }
                            livroAdapter.atualizarLista(listaDeLivros)
                        } else {
                            mostrarEstadoVazio()
                        }
                    }
                    .addOnFailureListener { e ->
                        if (isAdded) {
                            Toast.makeText(requireContext(), R.string.erro_carregar_dados, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    }

    private fun mostrarEstadoVazio() {
        binding.estadoVazio.visibility = View.VISIBLE
        binding.recyclerReservados.visibility = View.GONE
        livroAdapter.atualizarLista(emptyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}