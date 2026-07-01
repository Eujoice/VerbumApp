package com.example.verbumteste

import com.google.firebase.Timestamp

data class Historico(
    val data_devolucao_real: Timestamp? = null,
    val data_retirada: Timestamp? = null,
    val nome_usuario: String = "",
    val obra_id: String = "",
    val titulo_obra: String = "",
    val usuario_id: String = ""
)
