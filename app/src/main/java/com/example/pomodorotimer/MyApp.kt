package com.example.pomodorotimer

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


sealed class Screen(val route: String, @StringRes val title: Int, val icon: ImageVector) {
    object Home : Screen("Home", R.string.home, Icons.Default.Home)
    object Profile : Screen("Profile", R.string.profil, Icons.Default.Person)
    object Favourites : Screen("Favourites", R.string.favourites, Icons.Default.Favorite)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Myapp(myViewModel: MyViewModel = viewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uiState = myViewModel.uiState

    val bottomNavigationScreens = listOf(
        Screen.Favourites,
        Screen.Home,
        Screen.Profile
    )

    Scaffold(bottomBar = { BottomNavigationBar(navController, bottomNavigationScreens) },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->

        val mod = Modifier.padding(padding)

        NavHost(navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                val screen = Screen.Home
                GenericScreen(screen.title, uiState.value, screen.icon, mod)
            }
            composable(Screen.Profile.route) {
                val screen = Screen.Profile
                GenericScreen(screen.title, uiState.value, screen.icon, mod)
            }
            composable(Screen.Favourites.route) {
                val screen = Screen.Favourites
                GenericScreen(screen.title, uiState.value, screen.icon, mod)
            }
        }
    }
}

@Composable
fun GenericScreen(@StringRes screenName: Int, counterText: String, icon: ImageVector, modifier: Modifier) {
    // Her kan du bygge innholdet for hver skjerm uten å inkludere telleren
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    bottomNavigationScreens: List<Screen>
) {
    NavigationBar() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route

        bottomNavigationScreens.forEach { screen ->
            val title = stringResource(screen.title)

            NavigationBarItem(selected = currentDestination == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = screen.icon,
                        contentDescription = title)
                },
                label = { Text(title) })
        }
    }
}