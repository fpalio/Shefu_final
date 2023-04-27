package com.francisco.paliouras.shefu_final.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.francisco.paliouras.shefu_final.data.entities.Recipe
import com.francisco.paliouras.shefu_final.data.repositories.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application): AndroidViewModel(application) {
    private val recipeRepo : RecipeRepository = RecipeRepository(application)

    fun getAllRecipes(): LiveData<List<Recipe>>{
        return recipeRepo.getAllRecipes
    }

    fun getRecipeById(id: Int): LiveData<Recipe> {
        return recipeRepo.getRecipeById(id)
    }

    fun insertRecipes(recipe: Recipe){
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            recipeRepo.insertRecipe(recipe = recipe)
        }
    }

    fun deleteRecipeById(id: Int){
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            recipeRepo.deleteRecipeById(id)
        }
    }

    fun deleteAllRecipes(){
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            recipeRepo.deleteAllRecipes()
        }
    }
}