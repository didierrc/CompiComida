package com.example.compicomida.viewmodels.grocery.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.viewmodels.grocery.GroceryItemDetailsViewModel

class GroceryItemDetailsViewModelFactory(
    private val groceryRepo: GroceryRepository,
    private val pantryRepo: PantryRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryItemDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryItemDetailsViewModel(groceryRepo, pantryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}