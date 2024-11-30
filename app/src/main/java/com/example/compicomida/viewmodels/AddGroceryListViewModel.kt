package com.example.compicomida.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.GroceryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddGroceryListViewModel(private val groceryRepo: GroceryRepository) : ViewModel() {

    fun addGroceryList(
        listName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (listName.isBlank()) {
            onError("El nombre de la lista no puede estar vacío")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val existingList = groceryRepo.getGroceryListByName(listName)
            if (existingList != null) {
                withContext(Dispatchers.Main) {
                    onError("La lista ya existe")
                }
            } else {
                groceryRepo.addGroceryList(listName)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }
}