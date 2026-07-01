package com.example.verbumteste

data class Historico(
    val data_devolucao_real: String? = null,
    val data_retirada: String? = null,
    val nome_usuario: String = "",
    val obra_id: String = "",
    val titulo_obra: String = "",
    val usuario_id: String = "",
    var capa_obra: String? = null
)
