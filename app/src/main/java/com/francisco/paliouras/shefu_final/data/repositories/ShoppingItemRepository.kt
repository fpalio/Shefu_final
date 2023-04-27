package com.francisco.paliouras.shefu_final.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.francisco.paliouras.shefu_final.data.AppDatabase
import com.francisco.paliouras.shefu_final.data.dao.ShoppingItemDao
import com.francisco.paliouras.shefu_final.data.entities.ShoppingItem

class ShoppingItemRepository (application: Application) {

    private var shoppingItemDao: ShoppingItemDao

    init{
        val db = AppDatabase.getDatabase(application)
        shoppingItemDao = db.ShoppingItemDao()
    }

    //this is not a suspend function due to the nature of live data being async already
    val getAllShoppingItems: LiveData<List<ShoppingItem>> = shoppingItemDao.getAllShoppingItems()

    suspend fun insertShoppingItem(item: ShoppingItem){
        shoppingItemDao.insertShoppingItem(shoppingItem = item)
    }

    suspend fun deleteShoppingItemById(id: Int){
        shoppingItemDao.deleteShoppingItemById(id)
    }

    suspend fun deleteAllShoppingItems(){
        shoppingItemDao.deleteAllShoppingItems()
    }

}