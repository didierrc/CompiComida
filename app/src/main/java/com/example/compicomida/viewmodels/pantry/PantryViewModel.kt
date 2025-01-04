package com.example.compicomida.viewmodels.pantry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.localDb.entities.PantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PantryViewModel(
    private val pantryRepo: PantryRepository
) : ViewModel() {

    private val _pantryList = MutableLiveData<List<PantryItem>>()


    val pantryList: LiveData<List<PantryItem>>
        get() = _pantryList


    init {
        refreshPantryList()
    }

    fun refreshPantryList() {
        viewModelScope.launch(Dispatchers.IO) {
            pantryRepo.getPantryItems()?.let {
                _pantryList.postValue(it)
            }
        }
    }
}