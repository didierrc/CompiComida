package com.example.compicomida.viewmodels.grocery.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.grocery.AddGroceryItemViewModel

class AddGroceryItemViewModelFactory(
    private val groceryRepo: GroceryRepository,
    private val unitsArray: Array<String>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddGroceryItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddGroceryItemViewModel(groceryRepo, unitsArray) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}