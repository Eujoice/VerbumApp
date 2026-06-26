package com.example.verbumteste.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.verbumteste.R
import com.example.verbumteste.databinding.FragmentMinhaBibliotecaBinding
import com.google.firebase.firestore.FirebaseFirestore

class MinhaBibliotecaFragment : Fragment() {

    private var _binding: FragmentMinhaBibliotecaBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

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

        fun buscarReservados(matriculaUsuarioLogado: String) {

            if (matriculaUsuarioLogado.isEmpty()) return
            db.collection("reservas")
                .whereEqualTo("matricula", matriculaUsuarioLogado)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (!snapshots.isEmpty) {
                        for (documento in snapshots) {
                            val idLivroAssociado = documento.getString("obra_id")

                            if (idLivroAssociado != null) {
                                Log.d("FIRESTORE_RESERVA", "ID do Livro encontrado: $idLivroAssociado")

                                carregarReservados(idLivroAssociado)
                            }
                        }
                    }
                }
        }
    }


}