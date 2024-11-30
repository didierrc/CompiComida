package com.example.compicomida.viewmodels.uiData

import java.time.LocalDateTime

data class PantryItemUI(
    val itemNameTxt: String,
    val expirationDateValue: LocalDateTime,
    val quantityValue: Double,
    val unitTxt: String,
    val updateTime: LocalDateTime
)
