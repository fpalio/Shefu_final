package com.francisco.paliouras.shefu_final.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.francisco.paliouras.shefu_final.data.AppDatabase
import com.francisco.paliouras.shefu_final.data.dao.RecipeDao
import com.francisco.paliouras.shefu_final.data.entities.Direction
import com.francisco.paliouras.shefu_final.data.entities.Ingredient
import com.francisco.paliouras.shefu_final.data.entities.Recipe
import com.francisco.paliouras.shefu_final.data.entities.RecipeWithRelations

class RecipeRepository(application: Application) {

    private var recipeDao: RecipeDao

    init{
        val db = AppDatabase.getDatabase(application)
        recipeDao = db.RecipeDao()
    }

    //this is not a suspend function due to the nature of live data being async already
    val getAllRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()

    val getAllFavoriteRecipe : LiveData<List<Recipe>> = recipeDao.getAllFavoriteRecipes()
    fun getRecipeWithRelationsById(id: Int): LiveData<RecipeWithRelations> {
        return recipeDao.getRecipeWithRelationsById(id)
    }

    fun getRecipeById(id: Int): LiveData<Recipe> {
        return recipeDao.getRecipeById(id)
    }

    fun getRecipeDirectionsById(id: Int): LiveData<List<Direction>> {
        return recipeDao.getDirectionsForRecipe(id)
    }

    fun getRecipeIngredientsById(id: Int): LiveData<List<Ingredient>> {
        return recipeDao.getIngredientsForRecipe(id)
    }

    suspend fun insertRecipeWithRelations(recipeWithRelations: RecipeWithRelations): Long {
        val recipeId = recipeDao.insertRecipe(recipeWithRelations.recipe)
        recipeDao.insertIngredients(recipeWithRelations.ingredients.map { it.copy(recipe_id = recipeId.toInt()) })
        recipeDao.insertDirections(recipeWithRelations.directions.map { it.copy(recipe_id = recipeId.toInt()) })
        return recipeId
    }

    suspend fun deleteRecipeById(id: Int) {
        recipeDao.deleteRecipeById(id)
    }

    suspend fun deleteAllRecipes() {
        recipeDao.deleteAllRecipes()
    }

    suspend fun updateIsFavoriteById(id: Int, newIsFavorite: Boolean) {
        recipeDao.updateIsFavoriteById(id, newIsFavorite)
    }
}