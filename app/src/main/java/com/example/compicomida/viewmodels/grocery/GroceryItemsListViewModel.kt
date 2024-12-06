package com.example.compicomida.viewmodels.grocery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.localDb.entities.GroceryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroceryItemsListViewModel(
    private val groceryRepo: GroceryRepository,
    private val pantryRepo: PantryRepository,
    private val listID: Int
) : ViewModel() {

    private val _groceryItems = MutableLiveData<List<GroceryItem>>()
    val groceryItems: MutableLiveData<List<GroceryItem>>
        get() = _groceryItems

    private val _groceryListName = MutableLiveData<String>()
    val groceryListName: MutableLiveData<String>
        get() = _groceryListName

    // Refreshing the list of grocery items
    fun refreshGroceryItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _groceryItems.postValue(groceryRepo.getGroceryItemsByListId(listID))
        }
    }

    // Obtaining the name of the grocery list
    fun refreshGroceryListName() {
        viewModelScope.launch(Dispatchers.IO) {
            _groceryListName.postValue(groceryRepo.getGroceryListByID(listID)!!.listName)
        }
    }

    // Puts the isPurchased attribute of the item to the specified value
    fun checkItem(value: Boolean, itemID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            groceryRepo.checkGroceryItem(value, itemID)
            if(groceryRepo.getGroceryItemByID(itemID)!!.isPurchased){
                pantryRepo.addPantryItemsFromGroceryLists(groceryRepo.getGroceryItemByID(itemID)!!)
            }else{
                pantryRepo.deletePantryItemsFromGroceryLists(groceryRepo.getGroceryItemByID(itemID)!!)
            }
            refreshGroceryItems()
        }
    }
}