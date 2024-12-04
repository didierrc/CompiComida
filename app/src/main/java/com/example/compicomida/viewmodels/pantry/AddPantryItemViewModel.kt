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
    private val pantryRepo: PantryRepository
) : ViewModel() {

    private val _image = MutableLiveData(CompiComidaApp.DEFAULT_GROCERY_URI)
    val image: LiveData<String?>
        get() = _image

    fun updateImage(newImage: String?) {
        _image.value = newImage
    }

    fun addPantryItem(newPantry: PantryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            pantryRepo.addPantryItem(newPantry)
        }
    }


}