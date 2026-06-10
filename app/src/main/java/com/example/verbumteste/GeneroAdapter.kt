package com.example.verbumteste

data class GeneroSecao(
    val genero: String,
    val livros: List<Livro>
)
class GeneroAdapter (
    private val secoes: List<GeneroSecao>,
    private val onLivroClick: (Livro) -> Unit
)
