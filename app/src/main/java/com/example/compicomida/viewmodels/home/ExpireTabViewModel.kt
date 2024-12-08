package com.example.compicomida.viewmodels.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.localDb.entities.PantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpireTabViewModel(
    private val repoPantry: PantryRepository
) : ViewModel() {

    private val _expireList = MutableLiveData<List<PantryItem>>()
    val expireList: LiveData<List<PantryItem>>
        get() = _expireList

    init {
        refreshExpireList()
    }

    /**
     * Updating the items near to expire from the pantry.
     * DEFAULT - ALL
     */
    fun refreshExpireList() {
        viewModelScope.launch(Dispatchers.IO) {
            repoPantry.getCloseExpireItems().let {

                if (it == null) {
                    _expireList.postValue(listOf())
                    return@let
                }

                _expireList.postValue(it)
            }

        }
    }

    /**
     * Updating the items near to expire from the pantry.
     * FILTER - TODAY, TOMORROW, 2-DAYS
     */
    fun refreshExpireList(filter: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repoPantry.getCloseExpireItems(filter).let {
                if (it == null) {
                    _expireList.postValue(listOf())
                    return@let
                }
                _expireList.postValue(it)
            }
        }
    }

}