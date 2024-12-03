package com.example.compicomida.viewmodels.grocery.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.grocery.GroceryItemDetailsViewModel

class GroceryItemDetailsViewModelFactory(
    private val groceryRepo: GroceryRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryItemDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryItemDetailsViewModel(groceryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}