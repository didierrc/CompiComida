package com.example.compicomida.viewmodels

import androidx.lifecycle.ViewModel
import com.example.compicomida.model.GroceryRepository

class GroceryItemsListViewModel(
    private val groceryRepo: GroceryRepository
) : ViewModel() {
}