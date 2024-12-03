package com.example.compicomida.viewmodels.pantry.uiData

import java.time.LocalDateTime

data class PantryItemUI(
    val itemNameTxt: String,
    val expirationDateValue: LocalDateTime,
    val quantityValue: Double,
    val unitTxt: String,
    val updateTime: LocalDateTime
)
