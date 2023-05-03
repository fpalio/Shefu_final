package com.francisco.paliouras.shefu_final.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.francisco.paliouras.shefu_final.data.entities.Direction
import com.francisco.paliouras.shefu_final.data.entities.Ingredient
import com.francisco.paliouras.shefu_final.data.entities.Recipe
import com.francisco.paliouras.shefu_final.data.entities.RecipeWithRelations

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getRecipeWithRelationsById(id: Int): LiveData<RecipeWithRelations>

    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getRecipeById(id: Int): LiveData<Recipe>

    @Query("SELECT * FROM ingredient WHERE recipe_id = :id")
    fun getIngredientsForRecipe(id: Int): LiveData<List<Ingredient>>

    @Query("SELECT * FROM direction WHERE recipe_id = :id")
    fun getDirectionsForRecipe(id: Int): LiveData<List<Direction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredients(ingredients: List<Ingredient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDirections(directions: List<Direction>)

    @Query("SELECT * FROM recipe WHERE isFavorite = true")
    fun getAllFavoriteRecipes(): LiveData<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Query("DELETE FROM recipe WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    @Query("DELETE FROM recipe ")
    suspend fun deleteAllRecipes()

    @Query("UPDATE recipe SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateIsFavoriteById(id: Int, isFavorite: Boolean)
}
