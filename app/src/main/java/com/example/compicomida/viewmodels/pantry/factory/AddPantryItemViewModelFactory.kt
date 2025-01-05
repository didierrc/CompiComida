package com.example.compicomida.viewmodels.pantry.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.viewmodels.pantry.AddPantryItemViewModel

class AddPantryItemViewModelFactory(
    private val pantryRepo: PantryRepository,
    private val unitsArray: Array<String>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPantryItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPantryItemViewModel(pantryRepo, unitsArray) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}