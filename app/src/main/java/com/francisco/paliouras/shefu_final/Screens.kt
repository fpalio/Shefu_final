package com.francisco.paliouras.shefu_final

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.francisco.paliouras.shefu_final.data.entities.*
import com.francisco.paliouras.shefu_final.data.entities.Direction
import com.francisco.paliouras.shefu_final.models.RecipeViewModel
import com.francisco.paliouras.shefu_final.models.ShoppingItemViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask

enum class IngredientTypes(val stringValue: String) {
    GRAMS("Grams(g)"),
    POUNDS("Pounds (lb)"),
    MILLILITERS("Milliliters(ml)"),
    CUPS("Cups");
}

@Composable
fun DetailScreen(vm: RecipeViewModel, recipeId: Int, navigation: NavController, updateInAdd: (Boolean) -> Unit) {
    val recipeWithRelations by vm.getRecipeWithRelationsById(recipeId).observeAsState()
    val ingredients by vm.getIngredientsById(recipeId).observeAsState(listOf())
    val directions by vm.getDirectionsById(recipeId).observeAsState(listOf())

    val recipe = recipeWithRelations?.recipe

    if (recipe != null) {
        var recompose by remember {
            mutableStateOf(recipe.isFavorite)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                recipe.name?.let { Text(text = it, fontSize = 24.sp, fontWeight = FontWeight.Bold) }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Tags", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                recipe.tags?.let { Text(text = it.joinToString(", ")) }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Ingredients", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

                ingredients.forEach { ingredient ->
                    Text(text = "${ingredient.amount?.toString() ?: ""} ${ingredient.amountType.orEmpty()} ${ingredient.name.orEmpty()}")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Directions", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

                directions.sortedBy { it.index }
                    .forEachIndexed { index, direction ->
                        Text(text = "${index + 1}. ${direction.direction}")
                    }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row {
                    if (!recompose) {
                        Button(onClick = {
                            vm.updateIsFavorite(recipe.id, true)
                            recompose = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Add to Favorites"
                            )
                            Text("Add to Favorites")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Button(onClick = {
                            vm.updateIsFavorite(recipe.id, false)
                            recompose = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Remove from Favorites"
                            )
                            Text("Remove From Favorites")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(onClick = {
                        vm.deleteRecipeById(recipe.id)
                        navigation.navigate(NavDrawerItem.Home.route) {
                            navigation.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        updateInAdd(false)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                        Text("Delete")
                    }
                }
            }
            item{
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cooking Timer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(Modifier.height(30.dp))
                TimerComposable()
            }
        }
    } else {
        Text("No recipe selected", fontSize = 20.sp)
    }
}

@Composable
fun TimerComposable() {
    var timerState by remember { mutableStateOf(TimerState.Initial) }
    val timerText = remember { mutableStateOf("00:00") }
    val timerJob = rememberCoroutineScope()

    fun startTimer() {
        timerState = TimerState.Running
        timerJob.launch {
            var timeInSeconds = 0
            while (timerState == TimerState.Running) {
                delay(1000)
                timeInSeconds++
                val minutes = timeInSeconds / 60
                val seconds = timeInSeconds % 60
                timerText.value = String.format("%02d:%02d", minutes, seconds)
            }
        }
    }

    fun stopTimer() {
        timerState = TimerState.Stopped
    }

    fun resetTimer() {
        stopTimer()
        timerText.value = "00:00"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timerText.value,
            fontSize = 60.sp,
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { startTimer() },
                enabled = timerState != TimerState.Running
            ) {
                Text("Start")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { stopTimer() },
                enabled = timerState == TimerState.Running
            ) {
                Text("Stop")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = { resetTimer() }) {
                Text("Reset")
            }
        }
    }
}

enum class TimerState {
    Initial, Running, Stopped
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

    var directionInput by remember { mutableStateOf("") }
    var directions by remember { mutableStateOf(mutableListOf<Direction>()) }

    // Add new states for the ingredient input fields
    var ingredientName by remember { mutableStateOf("") }
    var ingredientAmount by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(IngredientTypes.GRAMS) }

    //This is the navigation stack.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        item{
            Text("Name")
            TextField(value = name, onValueChange = { name = it }, singleLine = true, modifier = Modifier.padding(8.dp))

            Text("Tags (comma separated)")
            TextField(value = tags.joinToString(", "), onValueChange = { newTags ->
                tags = newTags.split(',').map { it.trim() }.toMutableList()
            }, singleLine = true, modifier = Modifier.padding(8.dp))

            Text("Ingredients")
            Column {
                Row{
                    TextField(
                        value = ingredientName,
                        onValueChange = { ingredientName = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        placeholder = { Text("Ingredient Name") }
                    )
                    TextField(
                        value = ingredientAmount,
                        onValueChange = { ingredientAmount = it },
                        placeholder = { Text("Ingredient Amount") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Box(modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        expanded = !expanded
                    }) {
                    OutlinedTextField(
                        value = "Selected option: ${selectedOption.stringValue}",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
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
                            recipe_id = -1,
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

            Row{
                TextField(
                    value = directionInput,
                    onValueChange = { directionInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    placeholder = { Text("Input Direction") }
                )
                Button( modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp), onClick = {
                    if(directionInput.isNotEmpty() ){
                        val direction = Direction(
                            recipe_id = -1,
                            direction = directionInput,
                            index = null
                        )
                        directions.add(direction)

                        // Clear ingredient input fields
                        directionInput = ""
                        Log.d("TAG", "InsertRecipeScreen: $directions")
                        //dismiss keyboard
                        keyboardController?.hide()
                    }
                }) {
                    Text("Add Direction")
                }
            }
            DirectionsList(
                directions = directions,
                onRemoveDirection = { index ->
                    directions = directions.toMutableList().apply {
                        removeAt(index)
                    }.toMutableStateList()
                }
            )
        }


        item{

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    // Create a RecipeWithRelations object with the input data
                    val recipeWithRelations = RecipeWithRelations(
                        recipe = Recipe(
                            name = name,
                            tags = tags
                        ),
                        ingredients = ingredients,
                        directions = directions.mapIndexed { index, direction -> direction.copy(index = index) }
                    )

                    // Launch a coroutine to insert the recipe and update UI accordingly
                    vm.viewModelScope.launch {
                        val recipeId = vm.insertRecipeWithRelations(recipeWithRelations)

                        // Going back home after inserting recipe
                        navController.navigate(NavDrawerItem.Home.route) {
                            // We don't want to add a new destination to the stack every time
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // So we don't have multiple copies of the same destination
                            // when re-selecting the same item
                            launchSingleTop = true

                            // Restore the state when reselecting a previously selected
                            // item.
                            restoreState = true
                        }//navigate

                        // We update this so the button will reappear
                        updateInAdd(false)

                        // Clear all inputs
                        name = ""
                        tags.clear()
                        ingredients.clear()
                        directions.clear()
                    }
                }) {
                    Text("Add Recipe")
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DirectionsList(
    directions: List<Direction>,
    onRemoveDirection: (Int) -> Unit
) {
    for (index in directions.indices) {
        val direction = directions[index]

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${index + 1}. ${direction.direction}")

            IconButton(onClick = { onRemoveDirection(index) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove Direction")
            }
        }
    }
}

@Composable
fun IngredientsList(
    ingredients: List<Ingredient>,
    onRemoveIngredient: (Ingredient) -> Unit
) {
    Column(Modifier.padding(8.dp)) {
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
fun RecipeList(
    recipeViewModel: RecipeViewModel,
    navController: NavController,
    updateInAdd: (Boolean) -> Unit
) {
    val recipes by recipeViewModel.getAllRecipes().observeAsState(emptyList())

    if(recipes.isEmpty()){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Press + to add new Recipe",
                fontSize = 24.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
    }

    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(recipes) { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //Going back home after inserting recipe
                        //recipeViewModel.updateSelectedRecipe(recipe.id)
                        //toggles back button
                        updateInAdd(true)
                        navController.navigate("detail/${recipe.id}")
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

@Composable
fun FavoriteRecipeList(
    recipeViewModel: RecipeViewModel,
    navController: NavController,
    updateInAdd: (Boolean) -> Unit
) {
    val recipes by recipeViewModel.getAllFavoriteRecipes().observeAsState(emptyList())
    
    if(recipes.isEmpty()){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Add Recipe to Favorites to see in this list",
                fontSize = 24.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
    }

    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(recipes) { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        updateInAdd(true)
                        navController.navigate("detail/${recipe.id}")
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
fun ShoppingList(vm: ShoppingItemViewModel) {
    var textInput by remember { mutableStateOf("") }

    val itemList by vm.getAllShoppingItems().observeAsState(emptyList())

    Column(Modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Add to Checklist") }
            )
            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        vm.insertShoppingItem(ShoppingItem(name = textInput, checked = false))
                        textInput = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add")
            }
        }
        LazyColumn {
            items(itemList) { item ->
                Row(modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()){
                    item.name?.let {
                        Text(it, fontSize = 24.sp, modifier = Modifier.weight(1f))
                    }
                    Button(onClick = {
                        vm.deleteShoppingItemById(item.id)
                    }) {
                        Text(text = "Complete")
                    }
                }
            }
        }
    }
}