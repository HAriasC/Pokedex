package com.bks.pokedex.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Favorites : Screen("favorites")

    object Detail : Screen("detail/{pokemonName}/{pokemonId}") {
        fun createRoute(name: String, id: Int) = "detail/$name/$id"
    }
}
