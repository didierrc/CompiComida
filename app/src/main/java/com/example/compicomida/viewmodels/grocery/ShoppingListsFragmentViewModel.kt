package com.example.compicomida.viewmodels.grocery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.localDb.entities.GroceryList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListsFragmentViewModel(
    private val groceryRepo: GroceryRepository,
) : ViewModel() {

    private val _groceryLists = MutableLiveData<Map<GroceryList, Int>>()
    val groceryLists: LiveData<Map<GroceryList, Int>>
        get() = _groceryLists

    // Obtain all the grocery lists
    fun refreshGroceryLists() {
        viewModelScope.launch(Dispatchers.IO) {
            val lists = groceryRepo.getAllLists()
            val listsWithSize = mutableMapOf<GroceryList, Int>()
            for (list in lists) {
                val size = groceryRepo.getListSize(list.listId)
                listsWithSize[list] = size
            }
            withContext(Dispatchers.Main) {
                _groceryLists.postValue(listsWithSize)
            }
        }
    }

    // Delete a grocery list
    fun deleteGroceryList(groceryList: GroceryList?) {
        viewModelScope.launch(Dispatchers.IO) {
            groceryList?.let {
                groceryRepo.deleteGroceryList(it)
                refreshGroceryLists()
            }
        }

    }
}