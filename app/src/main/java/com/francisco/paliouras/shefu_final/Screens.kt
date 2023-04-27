package com.francisco.paliouras.shefu_final

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.francisco.paliouras.shefu_final.data.entities.Ingredient
import com.francisco.paliouras.shefu_final.data.entities.Recipe
import com.francisco.paliouras.shefu_final.models.RecipeViewModel

enum class IngredientTypes(val stringValue: String) {
    GRAMS("Grams(g)"),
    POUNDS("Pounds (lb)"),
    MILLILITERS("Milliliters(ml)"),
    CUPS("Cups");
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun InsertRecipeScreen(
    vm: RecipeViewModel,
    navController: NavHostController,
    updateInAdd: (Boolean) -> Unit
) {
    //keyboard controls
    val keyboardController = LocalSoftwareKeyboardController.current

    var name by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(mutableListOf<String>()) }
    var ingredients by remember { mutableStateOf(mutableStateListOf<Ingredient>()) }
    var directions by remember { mutableStateOf(mutableListOf<String>()) }

    // Add new states for the ingredient input fields
    var ingredientName by remember { mutableStateOf("") }
    var ingredientAmount by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(IngredientTypes.GRAMS) }

    //This is the navigation stack.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Name")
        TextField(value = name, onValueChange = { name = it }, singleLine = true)

        Text("Tags (comma separated)")
        TextField(value = tags.joinToString(", "), onValueChange = { newTags ->
            tags = newTags.split(',').map { it.trim() }.toMutableList()
        }, singleLine = true)

        Text("Ingredients")
        Column {
            Row{
                TextField(
                    value = ingredientName,
                    onValueChange = { ingredientName = it },
                    modifier = Modifier.weight(1f).padding(8.dp),
                    placeholder = { Text("Ingredient Name") }
                )
                TextField(
                    value = ingredientAmount,
                    onValueChange = { ingredientAmount = it },
                    placeholder = { Text("Ingredient Amount") },
                    modifier = Modifier.weight(1f).padding(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Box(modifier = Modifier.padding(8.dp).clickable{
                expanded = !expanded
            }) {
                OutlinedTextField(
                    value = "Selected option: ${selectedOption.stringValue}",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth().clickable{
                            expanded = !expanded
                        },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    },
                    readOnly = true
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .clickable { expanded = !expanded }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.wrapContentSize()
                ) {
                    IngredientTypes.values().forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedOption = option
                            expanded = false
                        }) {
                            Text(text = option.stringValue)
                        }
                    }
                }
            }

            Button(onClick = {
                   if(ingredientName.isNotEmpty() && ingredientAmount.isNotEmpty()){
                       val ingredient = Ingredient(
                           name = ingredientName,
                           amount = ingredientAmount.toDoubleOrNull(),
                           amountType = selectedOption.stringValue
                       )
                       ingredients.add(ingredient)

                       // Clear ingredient input fields
                       ingredientName = ""
                       ingredientAmount = ""
                       selectedOption = IngredientTypes.GRAMS

                       //dismiss keyboard
                       keyboardController?.hide()
                   }
                }) {
                    Text("Add Ingredient")
                }
        }

        IngredientsList(
            ingredients = ingredients,
            onRemoveIngredient = { ingredient ->
                ingredients = ingredients.toMutableList().apply {
                    remove(ingredient)
                }.toMutableStateList()
            }
        )

        Text("Directions (separate lines)")
        TextField(
            value = directions.joinToString("\n"),
            onValueChange = { newDirections ->
                directions = newDirections.split('\n').toMutableList()
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val recipe = Recipe(
                name = name,
                tags = tags,
                ingredients = ingredients,
                directions = directions
            )
            vm.insertRecipes(recipe)

            //Going back home after inserting recipe
            navController.navigate(NavDrawerItem.Home.route){
                //we dont want to add a new destination to the stack everytime
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) {
                        saveState = true
                    }
                }
                //so we don't have multiple copies of the same destination
                //when re-selecting the same item
                launchSingleTop = true

                //restore the state when reselecting a previously selected
                //item.
                restoreState = true
            }//navigate

            // we update this so the button will re appear
            updateInAdd(false)

            // Clear all inputs
            name = ""
            tags.clear()
            ingredients.clear()
            directions.clear()
        }) {
            Text("Add Recipe")
        }
    }
}

@Composable
fun IngredientsList(
    ingredients: List<Ingredient>,
    onRemoveIngredient: (Ingredient) -> Unit
) {
    Column {
        ingredients.forEach { ingredient ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${ingredient.name} - ${ingredient.amount} ${ingredient.amountType}",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { onRemoveIngredient(ingredient) }) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
fun RecipeList(recipeViewModel: RecipeViewModel, navController: NavController) {
    val recipes by recipeViewModel.getAllRecipes().observeAsState(emptyList())

    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(recipes) { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //Going back home after inserting recipe
                        navController.navigate(NavDrawerItem.Home.route) {
                            //we dont want to add a new destination to the stack everytime
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            //so we don't have multiple copies of the same destination
                            //when re-selecting the same item
                            launchSingleTop = true

                            //restore the state when reselecting a previously selected
                            //item.
                            restoreState = true
                        }//navigate
                    }
                    .padding(vertical = 8.dp)
            ) {
                Row {
                    RecipeInitialIcon(recipeName = recipe.name.orEmpty())
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Column {
                        Text(text = recipe.name.orEmpty(), fontSize = 20.sp)
                        Row {
                            Text(text = "Tags: ")
                            recipe.tags.orEmpty().forEach { tag ->
                                Text(text = tag)
                                Text(text = ",")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeInitialIcon(recipeName: String, modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.medium) {
    val firstLetter = recipeName.firstOrNull()?.uppercaseChar().toString()

    Box(
        modifier = modifier
            .size(48.dp)
            .background(MaterialTheme.colors.primary, shape)
    ) {
        Text(
            text = firstLetter,
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary
        )
    }
}

