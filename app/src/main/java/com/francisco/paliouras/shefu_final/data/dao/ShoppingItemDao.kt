package com.francisco.paliouras.shefu_final.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.francisco.paliouras.shefu_final.data.entities.ShoppingItem

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shoppingItem")
    fun getAllShoppingItems(): LiveData<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    @Query("DELETE FROM shoppingItem WHERE id = :id")
    suspend fun deleteShoppingItemById(id: Int)

    @Query("DELETE FROM shoppingItem ")
    suspend fun deleteAllShoppingItems()
}