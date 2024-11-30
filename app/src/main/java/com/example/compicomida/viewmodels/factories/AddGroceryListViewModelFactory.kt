package com.example.compicomida.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.AddGroceryListViewModel

class AddGroceryListViewModelFactory(
    private val groceryRepo: GroceryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddGroceryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddGroceryListViewModel(groceryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}