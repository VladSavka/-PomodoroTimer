package org.timer.main.breakactivity

import com.varabyte.truthish.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.timer.main.domain.video.WorkoutVideosGateway // Directly using the object
import kotlin.test.* // Using Kotlin Test

@ExperimentalCoroutinesApi
class BreakActivityViewModelTest {


    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: BreakActivityViewModel

    @BeforeTest // Kotlin Test equivalent of @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // ViewModel will be initialized here, and it will use the real WorkoutVideosGateway
        viewModel = BreakActivityViewModel()
    }

    @AfterTest // Kotlin Test equivalent of @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct with rootMenu and no back button`() = runTest {
        val initialState = viewModel.viewState.first()
        assertThat(initialState.currentMenu.title).isNull()
        assertThat(initialState.currentMenu.menuItems).hasSize(5)
        assertThat(initialState.currentMenu.menuItems.any { it.title == "Move Your Body" }).isTrue()
        assertThat(initialState.showBackButton).isFalse()
    }

    @Test
    fun `navigateTo item with children updates currentMenu and shows back button`() = runTest {
        val rootMenu = viewModel.viewState.first().currentMenu
        val moveYourBodyItem = rootMenu.menuItems.first { it.title == "Move Your Body" }

        viewModel.navigateTo(moveYourBodyItem)
        val newState = viewModel.viewState.first()

        val expectedChildMenu = moveYourBodyItem.children!!
        assertThat(newState.currentMenu.title).isEqualTo(expectedChildMenu.title)
        assertThat(newState.currentMenu.menuItems).isEqualTo(expectedChildMenu.menuItems)
        assertThat(newState.currentMenu.menuItems.any { it.title == "Workouts" }).isTrue()
        assertThat(newState.showBackButton).isTrue()
    }

    @Test
    fun `navigateTo item without children does not change currentMenu from parent and back button state remains`() = runTest {
        val rootMenu = viewModel.viewState.first().currentMenu
        val fuelUpParentItem = rootMenu.menuItems.first { it.title == "Fuel up" }

        viewModel.navigateTo(fuelUpParentItem)
        val fuelUpMenuState = viewModel.viewState.first()
        assertThat(fuelUpMenuState.currentMenu.title).isEqualTo("Fuel up")
        assertThat(fuelUpMenuState.showBackButton).isTrue()

        val getSomeWaterItem = fuelUpMenuState.currentMenu.menuItems.first { it.title == "Get some water" }

        viewModel.navigateTo(getSomeWaterItem)
        val finalState = viewModel.viewState.first()

        assertThat(finalState.currentMenu.title).isEqualTo("Fuel up")
        assertThat(finalState.currentMenu.menuItems).isEqualTo(fuelUpMenuState.currentMenu.menuItems)
        assertThat(finalState.showBackButton).isTrue()
    }

    @Test
    fun `onBackClick when stack is not empty navigates to previous menu and updates back button`() = runTest {
        val rootMenu = viewModel.viewState.first().currentMenu
        val moveYourBodyItem = rootMenu.menuItems.first { it.title == "Move Your Body" }

        viewModel.navigateTo(moveYourBodyItem)
        val afterNavigateState = viewModel.viewState.first()
        assertThat(afterNavigateState.currentMenu.menuItems.any { it.title == "Workouts" }).isTrue()
        assertThat(afterNavigateState.showBackButton).isTrue()

        viewModel.onBackClick()
        val afterBackClickState = viewModel.viewState.first()

        assertThat(afterBackClickState.currentMenu.title).isEqualTo(rootMenu.title)
        assertThat(afterBackClickState.currentMenu.menuItems).isEqualTo(rootMenu.menuItems)
        assertThat(afterBackClickState.showBackButton).isFalse()
    }

    @Test
    fun `onBackClick multiple times navigates up the stack correctly`() = runTest {
        val rootMenu = viewModel.viewState.first().currentMenu

        val moveYourBodyItem = rootMenu.menuItems.first { it.title == "Move Your Body" }
        val workoutsItem = moveYourBodyItem.children!!.menuItems.first { it.title == "Workouts" }
        val lowEnergyItem = workoutsItem.children!!.menuItems.first { it.title == "Low energy?" }

        viewModel.navigateTo(moveYourBodyItem)
        viewModel.navigateTo(workoutsItem)
        viewModel.navigateTo(lowEnergyItem)

        var currentState = viewModel.viewState.first()
        assertThat(currentState.currentMenu.title).isEqualTo("Chose your space")
        assertThat(currentState.showBackButton).isTrue()

        viewModel.onBackClick() // Back to energySelectionMenu
        currentState = viewModel.viewState.first()
        assertThat(currentState.currentMenu.title).isEqualTo(workoutsItem.children!!.title)
        assertThat(currentState.currentMenu.menuItems.any { it.title == "Low energy?" }).isTrue()
        assertThat(currentState.showBackButton).isTrue()

        viewModel.onBackClick() // Back to children of MYB
        currentState = viewModel.viewState.first()
        assertThat(currentState.currentMenu.title).isEqualTo(moveYourBodyItem.children!!.title)
        assertThat(currentState.currentMenu.menuItems.any { it.title == "Workouts" }).isTrue()
        assertThat(currentState.showBackButton).isTrue()

        viewModel.onBackClick() // Back to RootMenu
        currentState = viewModel.viewState.first()
        assertThat(currentState.currentMenu.menuItems).isEqualTo(rootMenu.menuItems)
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
    fun `exercisesMenu is correctly formed using real WorkoutVideosGateway vidos`() = runTest {
        val root = viewModel.viewState.first().currentMenu
        val moveYourBody = root.menuItems.first { it.title == "Move Your Body" }
        viewModel.navigateTo(moveYourBody)
        val workouts = viewModel.viewState.first().currentMenu.menuItems.first { it.title == "Workouts" }
        viewModel.navigateTo(workouts)
        val lowEnergy = viewModel.viewState.first().currentMenu.menuItems.first { it.title == "Low energy?" }
        viewModel.navigateTo(lowEnergy)
        val sitting = viewModel.viewState.first().currentMenu.menuItems.first { it.id == "1.1.1.1" } // This item's children is exercisesMenu
        viewModel.navigateTo(sitting)

        val exercisesMenuState = viewModel.viewState.first()
        val expectedVideos = WorkoutVideosGateway.getWorkoutVideos() // Using real data

        assertThat(exercisesMenuState.currentMenu.title).isEqualTo("Exercises")
        assertThat(exercisesMenuState.currentMenu.type).isEqualTo(Type.GRID)
        assertThat(exercisesMenuState.currentMenu.menuItems).hasSize(expectedVideos.size)

        // Verify each item from the gateway is present in the menu items
        expectedVideos.forEachIndexed { index, video ->
            val menuItem = exercisesMenuState.currentMenu.menuItems[index]
            assertThat(menuItem.id).isEqualTo(video.id.toString())
            assertThat(menuItem.title).isEqualTo(video.title)
        }
    }

    @Test
    fun `choose your tune is correctly formed using real WorkoutVideosGateway audios`() = runTest {
        val root = viewModel.viewState.first().currentMenu
        val moveYourBody = root.menuItems.first { it.title == "Move Your Body" }
        viewModel.navigateTo(moveYourBody)
        val danceBreak = viewModel.viewState.first().currentMenu.menuItems.first { it.title == "Dance Break" }
        viewModel.navigateTo(danceBreak)

        val exercisesMenuState = viewModel.viewState.first()
        val expectedAudios = WorkoutVideosGateway.getDanceAudios()

        assertThat(exercisesMenuState.currentMenu.title).isEqualTo("Choose your tune")
        assertThat(exercisesMenuState.currentMenu.type).isEqualTo(Type.AUDIO)
        assertThat(exercisesMenuState.currentMenu.menuItems).hasSize(expectedAudios.size)

        // Verify each item from the gateway is present in the menu items
        expectedAudios.forEachIndexed { index, audio ->
            val menuItem = exercisesMenuState.currentMenu.menuItems[index]
            assertThat(menuItem.id).isEqualTo(audio.id.toString())
            assertThat(menuItem.title).isEqualTo(audio.title)
            // Add more assertions for other MenuItem properties if needed (e.g., subtitle, icon)
        }
    }
}