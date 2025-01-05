package com.example.compicomida.viewmodels.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.R
import com.example.compicomida.model.GroceryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddGroceryListViewModel(private val groceryRepo: GroceryRepository) : ViewModel() {

    // Adding a new grocery list
    fun addGroceryList(
        listName: String,
        onSuccess: () -> Unit,
        onError: (Int) -> Unit
    ) {
        if (listName.isBlank()) {
            onError(R.string.add_grocery_list_vm_error_empty_listName)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val existingList = groceryRepo.getGroceryListByName(listName)
            if (existingList != null) {
                withContext(Dispatchers.Main) {
                    onError(R.string.add_grocery_list_vm_error_listAlreadyExists)
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