package com.francisco.paliouras.shefu_final

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.francisco.paliouras.shefu_final.models.RecipeViewModel
import com.francisco.paliouras.shefu_final.models.ShoppingItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(recipeViewModel: RecipeViewModel, shoppingItemViewModel: ShoppingItemViewModel) {
    
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    var inPage by remember {
        mutableStateOf(false)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                navController =
                navController,scope = scope,
                scaffoldState = scaffoldState,
                inPage = inPage,
                updateInPage = { newValue -> inPage = newValue}
            )
                 },
            drawerBackgroundColor = MaterialTheme.colors.primary,
            drawerContent = {
                Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
                            },
            bottomBar = { BottomBar(navController = navController, inPage = inPage,
                updateInPage = { newValue -> inPage = newValue}) }
    ) {
        //Main content
                Navigation(
                    navController = navController,
                    recipeViewModel = recipeViewModel,
                    shoppingItemViewModel = shoppingItemViewModel,
                    updateInAdd = { newValue -> inPage = newValue}
                )
    }//Scaffold
    
    
}

@Composable
fun BottomBar(modifier: Modifier = Modifier, navController: NavController, inPage: Boolean,
              updateInPage : (Boolean)-> Unit) {

    //This is the navigation stack.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box {
        BottomAppBar(
            backgroundColor = MaterialTheme.colors.primary,

        ) {
            // You can add other items to the bottom bar here
        }

        if(!inPage){
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavDrawerItem.AddRecipe.route){
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
                    updateInPage(true)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset((-10).dp, (-20).dp),
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        }
    }
}//BottomBar

@Composable
fun TopBar(
    navController: NavController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    inPage: Boolean,
    updateInPage : (Boolean)-> Unit
) {
    //This is the navigation stack.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentNavDrawerItem = getNavDrawerItemForRoute(currentRoute.toString())
    val title = currentNavDrawerItem?.title ?: "NotFound"
    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp) },
        navigationIcon = {
           if(!inPage){
               IconButton(onClick = {
                   scope.launch {
                       scaffoldState.drawerState.open()
                   }
               }) {
                   Icon(Icons.Filled.Menu, contentDescription = "")
               }
           }else{
               IconButton(onClick = {
                    //this will take you back to the previous screen
                   navController.navigateUp()
                   updateInPage(false)
               }) {
                   Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
               }
           }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    )
}


@Composable
fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
    //menu items
    val items = listOf(
        NavDrawerItem.Home,
        NavDrawerItem.Shopping,
        NavDrawerItem.Favorites
    )

    Column(modifier = Modifier.background(MaterialTheme.colors.primary)) {
        //Header
        Text(
            text = "Shefu.io",
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            fontSize = 24.sp, // Set the desired font size
            fontWeight = FontWeight.Bold, // Set the font weight to bold)
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(20.dp))

        //Navigation Items

        //This is the navigation stack.
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach {item ->
            DrawerItem(
                item = item,
                selected = currentRoute == item.route,
                onItemClick = {
                    navController.navigate(item.route) {
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

                    //after navigate, close the drawer
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                } )
        }
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Mobile Dev App 2 - Final Project",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )

    }//Column
}//Drawer

@Composable
fun DrawerItem(item: NavDrawerItem, selected: Boolean, onItemClick: (NavDrawerItem) -> Unit) {
    val background = if (selected) R.color.purple_700 else android.R.color.transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(item) })
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)

    ) {
        // Add the icon here
        val icon = when (item.icon) {
            "Home" -> Icons.Filled.Home
            "Cart" -> Icons.Filled.ShoppingCart
            "Heart" -> Icons.Filled.Favorite
            // Add more icons here based on the icon name
            else -> Icons.Filled.Home
        }
        Icon(
            imageVector = icon,
            contentDescription = "${item.title} icon",
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(7.dp))
        Text(text = item.title, fontSize = 18.sp, color = Color.White)
    }
}//DrawerItem


@Composable
fun Navigation(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    shoppingItemViewModel: ShoppingItemViewModel,
    updateInAdd: (Boolean) -> Unit,
) {
    NavHost(navController = navController, startDestination = NavDrawerItem.Home.route) {
        composable(NavDrawerItem.Home.route) {
            RecipeList(recipeViewModel, navController= navController)
        }
        composable(NavDrawerItem.AddRecipe.route) {
            InsertRecipeScreen(recipeViewModel, navController = navController, updateInAdd = updateInAdd)
        }
        composable(NavDrawerItem.Details.route){
            //TODO
        }
        composable(NavDrawerItem.Favorites.route){
            //TODO
        }
        composable(NavDrawerItem.Shopping.route){
            //TODO
        }
    }
}//Navigation