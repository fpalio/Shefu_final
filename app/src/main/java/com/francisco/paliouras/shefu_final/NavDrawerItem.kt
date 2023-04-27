package com.francisco.paliouras.shefu_final

sealed class NavDrawerItem(var route: String, var title: String, var icon : String) {
    object Home : NavDrawerItem("home",  "Home", "Home")
    object AddRecipe : NavDrawerItem("addRecipe", "Add Recipe", "Cart")
    object Shopping : NavDrawerItem("shopping", "Shopping List", "Cart")
    object Favorites : NavDrawerItem("favorites", "Favorites", "Heart")
    object Details : NavDrawerItem("details", "Detail View", "Details")
}//NavDrawerItem

fun getNavDrawerItemForRoute(route: String?): NavDrawerItem? {
    // Replace the example routes and titles with your actual routes and titles
    val navDrawerItems = listOf(
        NavDrawerItem.Home,
        NavDrawerItem.AddRecipe,
        NavDrawerItem.Shopping,
        NavDrawerItem.Favorites,
        NavDrawerItem.Details
    )

    return navDrawerItems.firstOrNull { it.route == route }
}