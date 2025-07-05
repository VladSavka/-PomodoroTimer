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
            navigateToScreen(navController, MobileRouts.Activities)
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
                startDestination = MobileRouts.Home.destanation,
            ) {
                composable(MobileRouts.Home.destanation) {
                    TimerScreen(viewModel = timerViewModel)
                }
                composable(MobileRouts.Activities.destanation) {
                    BreakActivityScreen(timerViewModel)
                }
                composable(MobileRouts.Settings.destanation) {
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
            MobileRouts.Home,
            MobileRouts.Activities,
            MobileRouts.Settings,
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
    screen: MobileRouts,
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
    screen: MobileRouts
) {
    navController.navigate(screen.destanation, {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    })
}

sealed class MobileRouts(
    val destanation: String,
    val resourceId: String? = null,
    val icon: DrawableResource? = null
) {
    data object Home : MobileRouts("timer", "Timer", Res.drawable.home)
    data object Activities : MobileRouts("activities", "Activities", Res.drawable.run)
    data object Settings : MobileRouts("settings", "Settings", Res.drawable.settings)
}