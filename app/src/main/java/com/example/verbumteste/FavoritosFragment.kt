package com.example.verbumteste

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verbumteste.databinding.FragmentFavoritosBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private var idUsuarioLogado: String = ""

    private lateinit var livroAdapter: LivroAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Captura o id do usuário logado
        val prefs = requireContext().getSharedPreferences("verbum_prefs", Context.MODE_PRIVATE)
        idUsuarioLogado = prefs.getString("id_usuario_logado", "") ?: ""

        binding.recyclerFavoritos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inicializa o adaptador para que com o clique abra os detalhes
        livroAdapter = LivroAdapter(mutableListOf(), onItemClick = { livroClicado ->
            val bundle = Bundle().apply {
                putSerializable("CHAVE_LIVRO", livroClicado)
            }
            findNavController().navigate(R.id.action_favoritosFragment_to_detalhesLivroFragment, bundle)
        })

        binding.recyclerFavoritos.adapter = livroAdapter

        if (idUsuarioLogado.isNotEmpty()) {
            buscarLivrosFavoritados()
        } else {
            binding.estadoVazio.visibility = View.VISIBLE
            Toast.makeText(requireContext(), R.string.realize_login, Toast.LENGTH_SHORT).show()
        }

        binding.btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun buscarLivrosFavoritados() {
        db.collection("favoritos")
            .whereEqualTo("usuario_id", idUsuarioLogado)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Toast.makeText(requireContext(), "Erro ao atualizar favoritos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    binding.estadoVazio.visibility = View.VISIBLE
                    binding.recyclerFavoritos.visibility = View.GONE
                    livroAdapter.atualizarLista(emptyList())
                    return@addSnapshotListener
                }


                // Mapeia os campos "obra_id" da coleção favoritos deste usuário
                val idsDosLivros = snapshot.documents.mapNotNull { it.getString("obra_id") }

                if (idsDosLivros.isEmpty()) {
                    binding.estadoVazio.visibility = View.VISIBLE
                    binding.recyclerFavoritos.visibility = View.GONE
                    livroAdapter.atualizarLista(emptyList())
                    return@addSnapshotListener
                }

                db.collection("obras")
                    .whereIn(FieldPath.documentId(), idsDosLivros)
                    .get()
                    .addOnSuccessListener { resultadoLivros ->
                        val listaDeLivros = resultadoLivros.documents.mapNotNull { doc ->
                            doc.toObject(Livro::class.java)?.copy(id = doc.id)
                        }.toMutableList()

                        if (listaDeLivros.isNotEmpty()) {
                            binding.estadoVazio.visibility = View.GONE
                            binding.recyclerFavoritos.visibility = View.VISIBLE

                            listaDeLivros.sortBy { it.titulo }
                            livroAdapter.atualizarLista(listaDeLivros)
                        } else {
                            binding.estadoVazio.visibility = View.VISIBLE
                            binding.recyclerFavoritos.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                    }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

}