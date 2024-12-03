package com.example.compicomida.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.grocery.ShoppingListsFragmentViewModel

class ShoppingListsFragmentViewModelFactory(
    private val groceryRepo: GroceryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListsFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListsFragmentViewModel(groceryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}