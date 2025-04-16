package org.timer.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import org.timer.main.settings.*
import org.timer.main.projects.*
import org.timer.main.timer.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileMainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = MainRouts.Home.destanation,
            ) {
                composable(MainRouts.Home.destanation) {
                    TimerScreen()
                }
                composable(MainRouts.Profile.destanation) {
                    ProjectsScreen()
                }
                composable(MainRouts.Settings.destanation) {
                    SettingsDialogScreen(isDialogVisible = {})
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        MainRouts.Home,
        MainRouts.Profile,
        MainRouts.Settings,
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route ?: ""

    val bottomBarDestination = screens.any { it.destanation == currentDestination }
    if (bottomBarDestination) {
        NavigationBar {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: MainRouts,
    currentDestination: String,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
        label = { screen.resourceId?.let { Text(it) } },
        selected = currentDestination == screen.destanation,
        onClick = {
            if (currentDestination == screen.destanation) {
                return@NavigationBarItem
            }
            navController.navigate(screen.destanation, {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            })
        }
    )
}

sealed class MainRouts(
    val destanation: String,
    val resourceId: String? = null,
    val icon: ImageVector? = null
) {
    data object Home : MainRouts("home", "Timer", Icons.Filled.Home)
    data object Profile : MainRouts("profile", "Tasks", Icons.Filled.CheckCircle)
    data object Settings : MainRouts("settings", "Settings", Icons.Filled.Settings)
}