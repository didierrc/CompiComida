package com.example.compicomida.viewmodels.grocery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.R
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.viewmodels.grocery.uiData.GroceryItemDetailsUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroceryItemDetailsViewModel(
    private val groceryRepo: GroceryRepository
) : ViewModel() {

    private val _groceryItem by lazy {
        val groceryItem = GroceryItemDetailsUI(
            itemNameTxt = "",
            itemCategory = "",
            priceTxt = "",
            unitsTxt = "",
            checkState = false,
            imageURI = ""
        )
        MutableLiveData(groceryItem)
    }

    val groceryItemUIState: LiveData<GroceryItemDetailsUI>
        get() = _groceryItem

    fun checkItem(value: Boolean, itemID: Int) {
        _groceryItem.value = _groceryItem.value!!.copy(checkState = value)
        viewModelScope.launch(Dispatchers.IO) {
            groceryRepo.checkItem(_groceryItem.value!!.checkState, itemID)
        }
    }

    fun refreshGroceryItemDetails(itemID: Int, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val groceryItem = groceryRepo.getGroceryItemByID(itemID) ?: return@launch
            val category =
                groceryRepo.getGroceryItemCategoryByID(groceryItem.categoryId!!)?.categoryName
                    ?: "Sin Categoría"
            _groceryItem.postValue(
                GroceryItemDetailsUI(
                    itemNameTxt = groceryItem.itemName,
                    itemCategory = category,
                    priceTxt = if (groceryItem.unit.isNullOrEmpty()) {
                        context.getString(
                            R.string.price_no_unit,
                            groceryItem.price
                        )
                    } else {
                        context.getString(
                            R.string.price_per_unit,
                            groceryItem.price,
                            groceryItem.unit
                        )
                    },
                    unitsTxt = context.getString(
                        R.string.quantity_unit,
                        groceryItem.quantity.toString(),
                        groceryItem.unit ?: "Unidades"
                    ),
                    checkState = groceryItem.isPurchased,
                    imageURI = groceryItem.itemPhotoUri
                )
            )
        }
    }

    fun removeGroceryItem(itemID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            groceryRepo.deleteGroceryItem(itemID)
        }
    }
}