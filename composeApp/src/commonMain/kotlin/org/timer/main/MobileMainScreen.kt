package org.timer.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import moe.tlaster.precompose.navigation.*
import moe.tlaster.precompose.navigation.transition.*
import org.timer.main.settings.*
import org.timer.main.tasks.*
import org.timer.main.timer.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileMainScreen() {
    val navController = Navigator()
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navigator = navController,
                navTransition = NavTransition(),
                initialRoute = MainRouts.Home.destanation,
            ) {
                scene(MainRouts.Home.destanation, navTransition = NavTransition()) {
                    TimerScreen()
                }
                scene(MainRouts.Profile.destanation, navTransition = NavTransition()) {
                    TasksScreen()
                }
                scene(MainRouts.Settings.destanation, navTransition = NavTransition()) {
                   SettingsDialogScreen(isDialogVisible = {})
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: Navigator) {
    val screens = listOf(
        MainRouts.Home,
        MainRouts.Profile,
        MainRouts.Settings,
    )
    val currentEntry by navController.currentEntry.collectAsState(null)
    val currentDestination = currentEntry?.route?.route ?: ""

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
    navController: Navigator
) {
    NavigationBarItem(
        icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
        label = { screen.resourceId?.let { Text(it) } },
        selected = currentDestination == screen.destanation,
        onClick = {
            if (currentDestination == screen.destanation) {
                return@NavigationBarItem
            }
            navController.navigate(
                screen.destanation, NavOptions(
                    launchSingleTop = true,
                )
            )
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