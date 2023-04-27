package com.francisco.paliouras.shefu_final.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.francisco.paliouras.shefu_final.data.entities.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getRecipeById(id: Int): LiveData<Recipe>

    @Query("SELECT * FROM recipe WHERE isFavorite = true")
    fun getAllFavoriteRecipes(): LiveData<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    @Query("DELETE FROM recipe ")
    suspend fun deleteAllRecipes()

    @Query("UPDATE recipe SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateIsFavoriteById(id: Int, isFavorite: Boolean)
}