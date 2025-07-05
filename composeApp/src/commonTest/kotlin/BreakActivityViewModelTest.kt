package org.timer.main.breakactivity

import com.varabyte.truthish.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.timer.main.domain.video.*
import kotlin.test.*

@ExperimentalCoroutinesApi
class BreakActivityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: BreakActivityViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // ViewModel will be initialized here, and it will use the real WorkoutVideosGateway
        viewModel = BreakActivityViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct with rootMenu and no back button`() = runTest {
        val initialState = viewModel.viewState.first()
        assertThat(initialState.screenContent).isInstanceOf<ScreenContent.Menu>()
        val screenContent = initialState.screenContent as ScreenContent.Menu

        assertThat(screenContent.currentMenu.title).isNull()
        assertThat(screenContent.currentMenu.menuItems).hasSize(5)
        assertThat(screenContent.currentMenu.menuItems.any { it.title == "Move Your Body" }).isTrue()
        assertThat(initialState.showBackButton).isFalse()
    }

    @Test
    fun `navigateTo item with children updates currentMenu and shows back button`() = runTest {
        val rootMenu = getCurrentMenu()
        val moveYourBodyItem = rootMenu.menuItems.first { it.title == "Move Your Body" }

        viewModel.navigateTo(moveYourBodyItem)
        val newState = viewModel.viewState.first()

        val expectedChildMenu = moveYourBodyItem.children!!
        val screenContent = newState.screenContent as ScreenContent.Menu
        assertThat(screenContent.currentMenu.title).isEqualTo(expectedChildMenu.title)
        assertThat(screenContent.currentMenu.menuItems).isEqualTo(expectedChildMenu.menuItems)
        assertThat(screenContent.currentMenu.menuItems.any { it.title == "Workouts" }).isTrue()
        assertThat(newState.showBackButton).isTrue()
    }

    @Test
    fun `navigateTo item without children does not change currentMenu from parent and back button state remains`() =
        runTest {
            val rootMenu =
                getCurrentMenu()
            val fuelUpParentItem = rootMenu.menuItems.first { it.title == "Fuel up" }

            viewModel.navigateTo(fuelUpParentItem)
            val fuelUpMenuState = viewModel.viewState.first()
            val currentMenu = (fuelUpMenuState.screenContent as ScreenContent.Menu).currentMenu
            assertThat(currentMenu.title).isEqualTo("Fuel up")
            assertThat(fuelUpMenuState.showBackButton).isTrue()

            val getSomeWaterItem = currentMenu.menuItems.first { it.title == "Get some water" }

            viewModel.navigateTo(getSomeWaterItem)
            val finalState = viewModel.viewState.first()

            val nextMenu = (fuelUpMenuState.screenContent as ScreenContent.Menu).currentMenu

            assertThat(nextMenu.title).isEqualTo("Fuel up")
            assertThat(nextMenu.menuItems).isEqualTo(currentMenu.menuItems)
            assertThat(finalState.showBackButton).isTrue()
        }

    @Test
    fun `onBackClick when stack is not empty navigates to previous menu and updates back button`() =
        runTest {
            val rootMenu =
                getCurrentMenu()
            val moveYourBodyItem = rootMenu.menuItems.first { it.title == "Move Your Body" }

            viewModel.navigateTo(moveYourBodyItem)
            val afterNavigateState = viewModel.viewState.first()
            val afterNavigateStateMenu =
                (afterNavigateState.screenContent as ScreenContent.Menu).currentMenu
            assertThat(afterNavigateStateMenu.menuItems.any { it.title == "Workouts" }).isTrue()
            assertThat(afterNavigateState.showBackButton).isTrue()

            viewModel.onBackClick()
            val afterBackClickState = viewModel.viewState.first()
            val afterBackClickStateMenu =
                (afterBackClickState.screenContent as ScreenContent.Menu).currentMenu
            assertThat(afterBackClickStateMenu.title).isEqualTo(rootMenu.title)
            assertThat(afterBackClickStateMenu.menuItems).isEqualTo(rootMenu.menuItems)
            assertThat(afterBackClickState.showBackButton).isFalse()
        }

    @Test
    fun `onBackClick multiple times navigates up the stack correctly`() = runTest {
        val rootMenu = getCurrentMenu()

        val moveYourBodyItem = rootMenu.menuItems.first { it.title == "Move Your Body" }
        val workoutsItem = moveYourBodyItem.children!!.menuItems.first { it.title == "Workouts" }
        val lowEnergyItem = workoutsItem.children!!.menuItems.first { it.title == "Low energy?" }

        viewModel.navigateTo(moveYourBodyItem)
        viewModel.navigateTo(workoutsItem)
        viewModel.navigateTo(lowEnergyItem)

        var currentState = viewModel.viewState.first()
        var currentMenu = (currentState.screenContent as ScreenContent.Menu).currentMenu

        assertThat(currentMenu.title).isEqualTo("Chose your space")
        assertThat(currentState.showBackButton).isTrue()

        viewModel.onBackClick() // Back to energySelectionMenu
        currentState = viewModel.viewState.first()
        currentMenu = (currentState.screenContent as ScreenContent.Menu).currentMenu
        assertThat(currentMenu.title).isEqualTo(workoutsItem.children!!.title)
        assertThat(currentMenu.menuItems.any { it.title == "Low energy?" }).isTrue()
        assertThat(currentState.showBackButton).isTrue()

        viewModel.onBackClick() // Back to children of MYB
        currentState = viewModel.viewState.first()
        currentMenu = (currentState.screenContent as ScreenContent.Menu).currentMenu

        assertThat(currentMenu.title).isEqualTo(moveYourBodyItem.children!!.title)
        assertThat(currentMenu.menuItems.any { it.title == "Workouts" }).isTrue()
        assertThat(currentState.showBackButton).isTrue()

        viewModel.onBackClick() // Back to RootMenu
        currentState = viewModel.viewState.first()
        currentMenu = (currentState.screenContent as ScreenContent.Menu).currentMenu

        assertThat(currentMenu.menuItems).isEqualTo(rootMenu.menuItems)
        assertThat(currentState.showBackButton).isFalse()
    }


    @Test
    fun `onBackClick when stack is empty does not change state`() = runTest {
        val initialState = viewModel.viewState.first()
        assertThat(initialState.showBackButton).isFalse()

        viewModel.onBackClick()
        val afterBackClickState = viewModel.viewState.first()

        assertThat(afterBackClickState).isEqualTo(initialState)
    }

    @Test
    fun `exercisesMenu is correctly formed`() = runTest {
        val root = getCurrentMenu()
        val moveYourBody = root.menuItems.first { it.title == "Move Your Body" }
        viewModel.navigateTo(moveYourBody)
        var currentMenu =
            getCurrentMenu()
        val workouts = currentMenu.menuItems.first { it.title == "Workouts" }
        viewModel.navigateTo(workouts)
        currentMenu = getCurrentMenu()
        val lowEnergy = currentMenu.menuItems.first { it.title == "Low energy?" }
        viewModel.navigateTo(lowEnergy)
        currentMenu = getCurrentMenu()
        val sitting =
            currentMenu.menuItems.first { it.id == "1.1.1.1" } // This item's children is exercisesMenu
        viewModel.navigateTo(sitting)

        val exercisesMenuState = viewModel.viewState.first()
        currentMenu = (exercisesMenuState.screenContent as ScreenContent.Menu).currentMenu
        val expectedVideos = WorkoutVideosGateway.getWorkoutVideos() // Using real data

        assertThat(currentMenu.title).isEqualTo("Exercises")
        assertThat(currentMenu.type).isEqualTo(Type.GRID)
        assertThat(currentMenu.menuItems).hasSize(expectedVideos.size)

        // Verify each item from the gateway is present in the menu items
        expectedVideos.forEachIndexed { index, video ->
            val menuItem = currentMenu.menuItems[index]
            assertThat(menuItem.id).isEqualTo(video.id.toString())
            assertThat(menuItem.title).isEqualTo(video.title)
        }
    }

    @Test
    fun `choose your tune menu is correctly formed`() = runTest {
        val root = getCurrentMenu()
        val moveYourBody = root.menuItems.first { it.title == "Move Your Body" }
        viewModel.navigateTo(moveYourBody)
        var currentMenu =
            getCurrentMenu()

        val danceBreak = currentMenu.menuItems.first { it.title == "Dance Break" }
        viewModel.navigateTo(danceBreak)

        val exercisesMenuState = viewModel.viewState.first()
        currentMenu = (exercisesMenuState.screenContent as ScreenContent.Menu).currentMenu

        val expectedAudios = WorkoutVideosGateway.getDanceAudios()

        assertThat(currentMenu.title).isEqualTo("Choose your tune")
        assertThat(currentMenu.type).isEqualTo(Type.AUDIO)
        assertThat(currentMenu.menuItems).hasSize(expectedAudios.size)

        // Verify each item from the gateway is present in the menu items
        expectedAudios.forEachIndexed { index, audio ->
            val menuItem = currentMenu.menuItems[index]
            assertThat(menuItem.id).isEqualTo(audio.id.toString())
            assertThat(menuItem.title).isEqualTo(audio.title)
            // Add more assertions for other MenuItem properties if needed (e.g., subtitle, icon)
        }
    }

    @Test
    fun `selection of exercise menu item should navigate to video screen`() = runTest {
        val root = getCurrentMenu()
        val moveYourBody = root.menuItems.first { it.title == "Move Your Body" }
        viewModel.navigateTo(moveYourBody)

        val workouts = getCurrentMenu().menuItems.first { it.title == "Workouts" }
        viewModel.navigateTo(workouts)

        val lowEnergy = getCurrentMenu().menuItems.first { it.title == "Low energy?" }
        viewModel.navigateTo(lowEnergy)

        val sitting = getCurrentMenu().menuItems.first { it.title.contains("Sitting") }
        viewModel.navigateTo(sitting)

        val exercise = getCurrentMenu().menuItems.first { it.title == "Exercise 1" }
        viewModel.navigateTo(exercise)
        val expectedVideo =
            WorkoutVideosGateway.getWorkoutVideos().first { it.id.toString() == exercise.id }
        val viewState = viewModel.viewState.first()
        assertThat(viewState.screenContent).isEqualTo(ScreenContent.VideoContent(expectedVideo))
        assertThat(viewState.showBackButton).isTrue()
    }

    @Test
    fun `selection of audio menu item should navigate to audio screen`() = runTest {
        val root = getCurrentMenu()
        val moveYourBody = root.menuItems.first { it.title == "Move Your Body" }
        viewModel.navigateTo(moveYourBody)

        val danceBreak = getCurrentMenu().menuItems.first { it.title == "Dance Break" }
        viewModel.navigateTo(danceBreak)

        val expectedAudio = WorkoutVideosGateway.getDanceAudios().first()
        val audio = getCurrentMenu().menuItems.first { it.title == expectedAudio.title }
        viewModel.navigateTo(audio)

        val viewState = viewModel.viewState.first()
        assertThat(viewState.screenContent).isEqualTo(ScreenContent.AudioContent(expectedAudio))
        assertThat(viewState.showBackButton).isTrue()
    }

    @Test
    fun `selection of make space tidy menu item should navigate to Go for It screen`() = runTest {
        val root = getCurrentMenu()
        val tidyUp = root.menuItems.first { it.title == "Tidy up" }
        viewModel.navigateTo(tidyUp)

        val makeSpaceTidy = getCurrentMenu().menuItems.first { it.title == "Make space tidy" }
        viewModel.navigateTo(makeSpaceTidy)

        val viewState = viewModel.viewState.first()
        assertThat(viewState.screenContent).isEqualTo(ScreenContent.GoForItContent)
        assertThat(viewState.showBackButton).isTrue()
    }

    @Test
    fun `on random wrokout click should navigate to video screen with random video`() = runTest {
        viewModel.onRandomWorkoutClick()
        val viewState = viewModel.viewState.first()
        assertThat(viewState.screenContent).isInstanceOf<ScreenContent.VideoContent>()
        assertThat(viewState.showBackButton).isTrue()
    }


    private suspend fun getCurrentMenu() =
        (viewModel.viewState.first().screenContent as ScreenContent.Menu).currentMenu
}