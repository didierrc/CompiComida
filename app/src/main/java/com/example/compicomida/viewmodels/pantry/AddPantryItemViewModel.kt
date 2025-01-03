package com.example.compicomida.viewmodels.pantry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.localDb.entities.PantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPantryItemViewModel(
    private val pantryRepo: PantryRepository,
    private val  unitsArray: Array<String>
) : ViewModel() {

    private val _image = MutableLiveData(CompiComidaApp.DEFAULT_GROCERY_URI)
    private val _units: MutableLiveData<MutableList<String>> =
        MutableLiveData(listOf<String>().toMutableList())

    val image: LiveData<String?>
        get() = _image

    val units: LiveData<MutableList<String>>
        get() = _units

    fun updateImage(newImage: String?) {
        _image.value = newImage
    }

    fun addPantryItem(newPantry: PantryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            pantryRepo.addPantryItem(newPantry)
        }
    }

    fun updateUnits() {
        viewModelScope.launch(Dispatchers.IO) {
            _units.postValue(unitsArray.toMutableList())
        }
    }


}