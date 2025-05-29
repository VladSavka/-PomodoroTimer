package org.timer.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.lifecycle.compose.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import org.jetbrains.compose.resources.*
import org.koin.compose.viewmodel.*
import org.timer.main.breakactivity.*
import org.timer.main.projects.*
import org.timer.main.settings.*
import org.timer.main.timer.*
import org.timer.ui.theme.*
import pomodorotimer.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileMainScreen(timerViewModel: TimerViewModel = koinViewModel()) {
    val navController = rememberNavController()

    val viewState by timerViewModel.viewState.collectAsStateWithLifecycle()
    LaunchedEffect(viewState.navigateToActivitiesScreen) {
        if (viewState.navigateToActivitiesScreen) {
            timerViewModel.onNavigatedToActivitiesScreen()
            navigateToScreen(navController, MainRouts.Activities)
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            NavHost(
                navController = navController,
                startDestination = MainRouts.Home.destanation,
            ) {
                composable(MainRouts.Home.destanation) {
                    TimerScreen(viewModel = timerViewModel)
                }
                composable(MainRouts.Activities.destanation) {
                    BreakActivityScreen(timerViewModel)
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
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkScheme else lightScheme) {
        val screens = listOf(
            MainRouts.Home,
            MainRouts.Activities,
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
}

@Composable
fun RowScope.AddItem(
    screen: MainRouts,
    currentDestination: String,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = {
            screen.icon?.let {
                Icon(
                    imageVector = vectorResource(it),
                    contentDescription = null
                )
            }
        },
        label = { screen.resourceId?.let { Text(it) } },
        selected = currentDestination == screen.destanation,
        onClick = {
            if (currentDestination == screen.destanation) {
                return@NavigationBarItem
            }
            navigateToScreen(navController, screen)
        }
    )
}

private fun navigateToScreen(
    navController: NavHostController,
    screen: MainRouts
) {
    navController.navigate(screen.destanation, {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    })
}

sealed class MainRouts(
    val destanation: String,
    val resourceId: String? = null,
    val icon: DrawableResource? = null
) {
    data object Home : MainRouts("timer", "Timer", Res.drawable.home)
    data object Activities : MainRouts("activities", "Activities", Res.drawable.run)
    data object Settings : MainRouts("settings", "Settings", Res.drawable.settings)
}