package com.example.compicomida.viewmodels.home.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.viewmodels.home.ExpireTabViewModel

class ExpireTabViewModelFactory(
    private val repoPantry: PantryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpireTabViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpireTabViewModel(repoPantry) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}