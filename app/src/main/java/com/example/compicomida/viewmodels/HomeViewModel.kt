package com.example.compicomida.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.PantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repoPantry: PantryRepository,
    private val repoGroceryList: GroceryRepository
) : ViewModel() {

    private val _expireList = MutableLiveData<List<PantryItem>>()
    val expireList: LiveData<List<PantryItem>>
        get() = _expireList

    private val _recentList = MutableLiveData<List<GroceryItem>>()
    val recentList: LiveData<List<GroceryItem>>
        get() = _recentList

    private val _recentListName = MutableLiveData<String?>()
    val recentListName: LiveData<String?>
        get() = _recentListName

    /**
     * Updating the items near to expire from the pantry.
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
     * Updating the grocery items from the most recent list and its name.
     */
    fun refreshRecentList() {
        viewModelScope.launch(Dispatchers.IO) {
            repoGroceryList.getItemsFromMostRecentList().let {

                val name = repoGroceryList.getMostRecentListName()

                if (name != null)
                    _recentListName.postValue(name)
                else
                    _recentListName.postValue("Nada para mostrar!")


                if (it == null) {
                    _recentList.postValue(listOf())
                    return@let
                }

                _recentList.postValue(it)
            }


        }
    }
}