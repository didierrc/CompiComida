package com.example.compicomida.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.viewmodels.pantry.EditPantryItemViewModel

class EditPantryItemViewModelFactory(
    private val pantryRepo: PantryRepository,
    private val pantryID: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditPantryItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditPantryItemViewModel(pantryRepo, pantryID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}