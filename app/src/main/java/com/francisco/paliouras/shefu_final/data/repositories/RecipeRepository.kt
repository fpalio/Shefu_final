package com.francisco.paliouras.shefu_final.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.francisco.paliouras.shefu_final.data.AppDatabase
import com.francisco.paliouras.shefu_final.data.dao.RecipeDao
import com.francisco.paliouras.shefu_final.data.entities.Recipe

class RecipeRepository(application: Application) {

    private var recipeDao: RecipeDao

    init{
        val db = AppDatabase.getDatabase(application)
        recipeDao = db.RecipeDao()
    }

    //this is not a suspend function due to the nature of live data being async already
    val getAllRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeById(id: Int): LiveData<Recipe> {
        return recipeDao.getRecipeById(id)
    }
    suspend fun insertRecipe(recipe: Recipe){
        recipeDao.insertRecipe(recipe = recipe)
    }

    suspend fun deleteRecipeById(id: Int){
        recipeDao.deleteRecipeById(id)
    }

    suspend fun deleteAllRecipes(){
        recipeDao.deleteAllRecipes()
    }

    suspend fun updateIsFavoriteById(id: Int, newIsFavorite: Boolean){
        recipeDao.updateIsFavoriteById(id,newIsFavorite)
    }
}