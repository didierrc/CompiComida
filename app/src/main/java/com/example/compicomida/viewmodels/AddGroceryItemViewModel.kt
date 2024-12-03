package com.example.compicomida.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.ItemCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddGroceryItemViewModel(private val groceryRepo: GroceryRepository) : ViewModel() {

    private val _categories: MutableLiveData<MutableList<String>> =
        MutableLiveData(listOf<String>().toMutableList())
    private val _itemCategory = MutableLiveData<ItemCategory?>(null)
    private val _image = MutableLiveData<String?>(null)
    val image: LiveData<String?>
        get() = _image

    val itemCategory: LiveData<ItemCategory?>
        get() = _itemCategory

    val categories: LiveData<MutableList<String>>
        get() = _categories

    fun updateImage(newImage: String?) {
        _image.value = newImage
    }

    fun updateItemCategory(newCategoryName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newCategoryName != null) {
                _itemCategory.postValue(groceryRepo.getItemCategory(newCategoryName))
            } else {
                _itemCategory.postValue(null)
            }
        }
    }

    fun updateCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _categories.postValue(groceryRepo.getAllCategories().map { it.categoryName }
                .toMutableList())
        }
    }

    fun addGroceryItem(newGrocery: GroceryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            groceryRepo.addGroceryItem(newGrocery)
        }
    }
}