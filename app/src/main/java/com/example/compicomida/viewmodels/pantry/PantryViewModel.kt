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

    // Needed an original list because of the following case:
    // Search "FRE" -> Shows "fresas" pantry -> Continue searching "FRE456" -> Not showing anything
    // -> Erase "456" -> Should show "fresas" pantry (if filtering on _pantryList, this is not possible)
    private val _originalList = MutableLiveData<List<PantryItem>>()

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
                _originalList.postValue(it)
            }
        }
    }

    fun refreshPantryList(query: String) {

        if (query.isEmpty())
            refreshPantryList()
        else {

            _originalList.value?.filter { pantryItem ->
                pantryItem.pantryName.lowercase().contains(query.lowercase())
            }?.let {
                _pantryList.postValue(it)
            }

            /* BETTER OPTION THAN QUERYING FROM DB!!! FILTERING OVER THE ALREADY RETRIEVED LIST
            THIS CASE WOULD BE BETTER IF WE HAVE CONCURRENCY
            viewModelScope.launch(Dispatchers.IO) {
                pantryRepo.getPantryItemsByTextQuery(query)?.let {
                    _pantryList.postValue(it)
                }
            }*/
        }
    }

}