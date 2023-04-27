package com.francisco.paliouras.shefu_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.francisco.paliouras.shefu_final.models.RecipeViewModel
import com.francisco.paliouras.shefu_final.ui.theme.Shefu_finalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        setContent {
            Shefu_finalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(recipeViewModel)
                }
            }
        }
    }
}

