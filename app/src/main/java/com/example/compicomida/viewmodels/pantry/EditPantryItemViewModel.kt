package com.example.compicomida.viewmodels.pantry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.viewmodels.pantry.uiData.PantryItemUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPantryItemViewModel(
    private val pantryRepo: PantryRepository,
    private val pantryID: Int,
    private val unitsArray: Array<String>
) : ViewModel() {

    private val _pantryItem = MutableLiveData<PantryItem?>()
    private val _units: MutableLiveData<MutableList<String>> =
        MutableLiveData(listOf<String>().toMutableList())

    val pantryItem: LiveData<PantryItem?>
        get() = _pantryItem
    val units: LiveData<MutableList<String>>
        get() = _units

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _pantryItem.postValue(pantryRepo.getPantryItemById(pantryID))
        }
    }

    fun deletePantryItem() {
        viewModelScope.launch(Dispatchers.IO) {
            _pantryItem.value?.let { pantryRepo.deletePantryItem(it) }
            _pantryItem.postValue(null)
        }
    }

    fun updateImage(imageUri: String?) {
        _pantryItem.value?.let {
            it.pantryPhotoUri = imageUri ?: CompiComidaApp.DEFAULT_GROCERY_URI
        }
    }

    fun updatePantryItem(editedPantryItem: PantryItemUI) {
        _pantryItem.value?.let {

            it.pantryName = editedPantryItem.itemNameTxt
            it.expirationDate = editedPantryItem.expirationDateValue
            it.quantity = editedPantryItem.quantityValue
            it.unit = editedPantryItem.unitTxt
            it.lastUpdate = editedPantryItem.updateTime
            // If image has been modified, method above has been called!

            viewModelScope.launch(Dispatchers.IO) {
                pantryRepo.updatePantryItem(it)
                _pantryItem.postValue(pantryRepo.getPantryItemById(pantryID)) // Refrescando el item
            }


        }
    }

    fun updateUnits() {
        viewModelScope.launch(Dispatchers.IO) {
            _units.postValue(unitsArray.toMutableList())
        }
    }

    //USO EXCLUSIVO PARA TEST!!!
    fun setTestPantryItem(item: PantryItem) {
        _pantryItem.postValue(item)
    }
}