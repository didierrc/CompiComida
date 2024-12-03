package com.example.compicomida.viewmodels.grocery.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.grocery.GroceryItemsListViewModel

class GroceryItemsListViewModelFactory(
    private val groceryRepo: GroceryRepository,
    private val listID: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryItemsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryItemsListViewModel(groceryRepo, listID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}