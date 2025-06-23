package org.timer.main.breakactivity

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import org.timer.main.domain.video.WorkoutVideosGateway.getWorkoutVideos

class BreakActivityViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(BreakActivityViewState())
    val viewState: StateFlow<BreakActivityViewState> = _viewState.asStateFlow()

    private val currentMenu: CurrentMenu
        get() = if (navigationStack.isEmpty()) rootMenu
        else navigationStack.last().children ?: CurrentMenu(menuItems = emptyList())

    private val navigationStack = mutableListOf<MenuItem>()


    private val exercisesMenu = CurrentMenu(
        "Exercises",
        getWorkoutVideos().map { MenuItem(it.id.toString(), it.title) },
        type = Type.GRID
    )

    private val choseYourSpaceMenu = CurrentMenu(
        title = "Chose your space", menuItems = listOf(
            MenuItem(id = "1.1.1.1", title = "Sitting", children = exercisesMenu),
            MenuItem(id = "1.1.1.2", title = "Standing 1", children = exercisesMenu),
            MenuItem(id = "1.1.1.2", title = "Standing 2", children = exercisesMenu),
            MenuItem(id = "1.1.1.2", title = "Floor time", children = exercisesMenu),
        )
    )

    private val energySelectionMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(
                id = "1.1.1",
                title = "Low energy?",
                subtitle = " 1 exercise for about one minute. Still Counts",
                children = choseYourSpaceMenu
            ),
            MenuItem(
                id = "1.1.2",
                title = "High Energy?",
                subtitle = "4  minutes of moderate to intense exercises",
                children = choseYourSpaceMenu
            ),
        )
    )

    private val fuelUpMenu = CurrentMenu(
        title = "Fuel up",
        menuItems = listOf(
            MenuItem(
                id = "1.1",
                title = "Get some water",
            ),
            MenuItem(
                id = "1.2",
                title = "Tea/coffee break",
            ),
            MenuItem(
                id = "1.3",
                title = "Snack time",
            ),
        )
    )


    private val rootMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(
                id = "1", title = "Move Your Body",
                children = CurrentMenu(
                    menuItems = listOf(
                        MenuItem(id = "1.1", title = "Workouts", children = energySelectionMenu),
                        MenuItem(id = "1.2", title = "Dance Break", children = energySelectionMenu),
                        MenuItem(
                            id = "1.3",
                            title = "Shake it out",
                            children = energySelectionMenu
                        ),
                        MenuItem(id = "1.4", title = "Eye Stretch", children = energySelectionMenu)
                    )
                )
            ),
            MenuItem(id = "2", title = "Fuel up", children = fuelUpMenu),
            MenuItem(id = "3", title = "Tidy up"),
            MenuItem(id = "4", title = "Clear your mind"),
            MenuItem(id = "5", title = "Mini challenge"),
        )
    )


    init {
        _viewState.update {
            it.copy(
                currentMenu = currentMenu,
                showBackButton = navigationStack.isNotEmpty()
            )
        }
    }

    fun navigateTo(item: MenuItem) {
        if (item.children != null) {
            navigationStack.add(item)
        } else {
            // Здесь можно выполнять действие, если пункт конечный
        }
        _viewState.update {
            it.copy(
                currentMenu = currentMenu,
                showBackButton = navigationStack.isNotEmpty()
            )
        }
    }

    fun onBackClick() {
        if (navigationStack.isNotEmpty()) {
            navigationStack.removeLast()
        }
        _viewState.update {
            it.copy(
                currentMenu = currentMenu,
                showBackButton = navigationStack.isNotEmpty()
            )
        }
    }
}