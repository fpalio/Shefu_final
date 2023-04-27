package com.francisco.paliouras.shefu_final.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.francisco.paliouras.shefu_final.data.entities.ShoppingItem
import com.francisco.paliouras.shefu_final.data.repositories.ShoppingItemRepository
import kotlinx.coroutines.launch

class ShoppingItemViewModel(application: Application): AndroidViewModel(application){
    private val shoppingItemRepo: ShoppingItemRepository = ShoppingItemRepository(application)

    fun getAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return shoppingItemRepo.getAllShoppingItems
    }

    fun insertShoppingItem(item: ShoppingItem){
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            shoppingItemRepo.insertShoppingItem(item = item)
        }
    }

    fun deleteShoppingItemById(id: Int){
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            shoppingItemRepo.deleteShoppingItemById(id)
        }
    }

    fun deleteAllShoppingItems(){
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            shoppingItemRepo.deleteAllShoppingItems()
        }
    }
}