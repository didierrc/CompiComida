package com.example.compicomida.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.AddGroceryItemViewModel

class AddGroceryItemViewModelFactory(
    private val groceryRepo: GroceryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddGroceryItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddGroceryItemViewModel(groceryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}