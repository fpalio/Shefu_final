package com.francisco.paliouras.shefu_final.models

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.francisco.paliouras.shefu_final.data.entities.Direction
import com.francisco.paliouras.shefu_final.data.entities.Ingredient
import com.francisco.paliouras.shefu_final.data.entities.Recipe
import com.francisco.paliouras.shefu_final.data.entities.RecipeWithRelations
import com.francisco.paliouras.shefu_final.data.repositories.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeRepo: RecipeRepository = RecipeRepository(application)

    val selectedRecipe = mutableStateOf<RecipeWithRelations?>(null)

    val selectedRecipeId = mutableStateOf(0)

    fun getAllRecipes(): LiveData<List<Recipe>> {
        return recipeRepo.getAllRecipes
    }

    fun getAllFavoriteRecipes(): LiveData<List<Recipe>> {
        return recipeRepo.getAllFavoriteRecipe
    }

    fun getRecipeWithRelationsById(id: Int): LiveData<RecipeWithRelations> {
        return recipeRepo.getRecipeWithRelationsById(id)
    }

    fun getRecipeById(id: Int): MutableLiveData<Recipe> {
        val recipeLiveData = MutableLiveData<Recipe>()
        viewModelScope.launch {
            val recipe = recipeRepo.getRecipeById(id).value
            recipeLiveData.value = recipe
        }
        return recipeLiveData
    }

    fun getIngredientsById(id:Int): LiveData<List<Ingredient>>{
        return recipeRepo.getRecipeIngredientsById(id)
    }

    fun getDirectionsById(id:Int): LiveData<List<Direction>>{
        return recipeRepo.getRecipeDirectionsById(id)
    }

    suspend fun insertRecipeWithRelations(recipeWithRelations: RecipeWithRelations): Long {
        return withContext(Dispatchers.IO) {
            val recipeId = recipeRepo.insertRecipeWithRelations(recipeWithRelations)
            recipeId
        }
    }

    fun deleteRecipeById(id: Int) {
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            recipeRepo.deleteRecipeById(id)
        }
    }

    fun deleteAllRecipes() {
        //calling the suspend function so we need a coroutine to do so
        viewModelScope.launch {
            recipeRepo.deleteAllRecipes()
        }
    }

    fun updateIsFavorite(id: Int, newIsFavorite: Boolean) {
        viewModelScope.launch {
            recipeRepo.updateIsFavoriteById(id, newIsFavorite)
        }
    }

    fun updateSelectedRecipe(recipeId: Int) {
        selectedRecipeId.value = recipeId
    }
}
