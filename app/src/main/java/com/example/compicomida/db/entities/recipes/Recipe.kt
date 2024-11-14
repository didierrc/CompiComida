package com.example.compicomida.db.entities.recipes

data class Recipe(
    val id: String? = null,
    val name: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val preparationTime: Int = 0,
    val difficulty: Int = 0,
    val description: String = "",
    val diner: Int = 0,
    val ingredients: List<Ingredient> = listOf(),
    val steps: List<String> = listOf()
)
