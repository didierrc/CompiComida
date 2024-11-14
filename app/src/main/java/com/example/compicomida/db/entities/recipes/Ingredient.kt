package com.example.compicomida.db.entities.recipes

data class Ingredient(
    val name: String = "",
    val quantity: Double? = null,
    val unit: String? = null
)
