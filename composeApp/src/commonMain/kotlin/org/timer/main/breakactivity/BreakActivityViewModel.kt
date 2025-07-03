package org.timer.main.breakactivity

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import org.kodein.emoji.*
import org.kodein.emoji.objects.household.*
import org.kodein.emoji.people_body.person_activity.*
import org.kodein.emoji.people_body.person_resting.*
import org.timer.main.domain.video.WorkoutVideosGateway.getDanceAudios
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
            MenuItem(
                id = "1.1.1.1",
                title = "${Emoji.Chair} Sitting",
                subtitle = "For cafes, coworking spaces, or Zoom calls.",
                children = exercisesMenu
            ),
            MenuItem(
                id = "1.1.1.2",
                title = "${Emoji.Standing} Standing. Low-Key Mode",
                subtitle = "Can move a little but don’t want to draw attention, or a tight spot",
                children = exercisesMenu
            ),
            MenuItem(
                id = "1.1.1.3",
                title = "${Emoji.ManDancing} Standing. Party animal",
                subtitle = "Got a little room to groove?",
                children = exercisesMenu
            ),
            MenuItem(
                id = "1.1.1.4",
                title = "${Emoji.Yoga} Floor Time",
                subtitle = "Private space? Let’s get on the floor.",
                children = exercisesMenu
            ),
        )
    )

    private val audioMenu = CurrentMenu(
        "Choose your tune",
        getDanceAudios().map { MenuItem(it.id.toString(), it.title) },
        type = Type.AUDIO
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


    private val moveYourBodyMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(id = "1.1", title = "Workouts", children = energySelectionMenu),
            MenuItem(id = "1.2", title = "Dance Break", children = audioMenu),
            MenuItem(id = "1.3", title = "Shake it out"),
            MenuItem(id = "1.4", title = "Eye Stretch")
        )
    )

    private val tidyUpMenu = CurrentMenu(
        title = "Tidy up",
        menuItems = listOf(
            MenuItem(id = "1.1", title = "Make space tidy"),
            MenuItem(id = "1.2", title = "Take out trash"),
            MenuItem(id = "1.3", title = "Your idea - you know what you are doing"),
        )
    )

    private val rootMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(
                id = "1", title = "Move Your Body",
                children = moveYourBodyMenu
            ),
            MenuItem(id = "2", title = "Fuel up", children = fuelUpMenu),
            MenuItem(id = "3", title = "Tidy up", children = tidyUpMenu),
            MenuItem(id = "4", title = "Clear your mind"),
            MenuItem(id = "5", title = "Mini challenge"),
        )
    )


    init {
        _viewState.update {
            it.copy(
                screenContent = ScreenContent.Menu(currentMenu),
                showBackButton = navigationStack.isNotEmpty()
            )
        }
    }

    fun navigateTo(item: MenuItem) {
        if (item.children != null) {
            navigationStack.add(item)
            _viewState.update {
                it.copy(
                    screenContent = ScreenContent.Menu(currentMenu),
                    showBackButton = navigationStack.isNotEmpty()
                )
            }
        } else {
            _viewState.update { it.copy(showBackButton = true) }
            navigateToNextScreen(item)
        }

    }

    private fun navigateToNextScreen(item: MenuItem) {
        val screenContent = _viewState.value.screenContent
        if (screenContent is ScreenContent.Menu) {
            when (screenContent.currentMenu.title) {
                "Exercises" -> {
                    val video = getWorkoutVideos().first { it.id.toString() == item.id }
                    _viewState.update { it.copy(screenContent = ScreenContent.VideoContent(video)) }
                }

                "Choose your tune" -> {
                    val audio = getDanceAudios().first { it.id.toString() == item.id }
                    _viewState.update { it.copy(screenContent = ScreenContent.AudioContent(audio)) }
                }

                "Tidy up" -> {
                    _viewState.update { it.copy(screenContent = ScreenContent.GoForItContent) }
                }

                else -> {
                    _viewState.update { it.copy(screenContent = ScreenContent.ToDoContent) }
                }
            }
        }
    }

    fun onBackClick() {
        if (_viewState.value.screenContent !is ScreenContent.Menu) {
            _viewState.update {
                it.copy(
                    screenContent = ScreenContent.Menu(currentMenu),
                    showBackButton = navigationStack.isNotEmpty()
                )
            }
            return
        }
        if (navigationStack.isNotEmpty()) {
            navigationStack.removeAt(navigationStack.lastIndex)
        }
        _viewState.update {
            it.copy(
                screenContent = ScreenContent.Menu(currentMenu),
                showBackButton = navigationStack.isNotEmpty()
            )
        }
    }

    fun onRandomWorkoutClick() {
        val video = getWorkoutVideos().random()
        _viewState.update {
            it.copy(
                screenContent = ScreenContent.VideoContent(video),
                showBackButton = true
            )
        }
    }

    fun navigateToRootMenu() {
        navigationStack.clear()
        _viewState.update {
            it.copy(
                screenContent = ScreenContent.Menu(currentMenu),
                showBackButton = navigationStack.isNotEmpty()
            )
        }
    }
}