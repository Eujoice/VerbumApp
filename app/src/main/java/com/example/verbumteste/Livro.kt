package com.example.verbumteste

import kotlinx.serialization.Serializable

data class Livro(
    val ano_publicacao: String ="",
    val autor: String ="",
    val avaliacao: String ="",
    val avaliacao_media: Double = 0.0,
    val capa: String ="",
    val colecao: String ="",
    val data_adicao: String ="",
    val data_devolucao_prevista: String ="",
    val data_emprestimo: String ="",
    val editora: String ="",
    val emprestado_por: String ="",
    val exemplares_totais: String ="",
    val genero: String ="",
    val id: String ="",
    val id_emprestimo_atual: String ="",
    val idioma_original: String ="",
    val isbn: String ="",
    val local_publicacao: String ="",
    val localizacao: String ="",
    val paginas: String ="",
    val resumo: String ="",
    val sinopse: String ="",
    val status: String ="",
    val titulo: String ="",
    val total_avaliacoes: Int = 0,
    val traducao: String =""
) : java.io.Serializable // Permite o envio dos dados da classe Livro
