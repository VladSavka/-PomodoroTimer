package org.timer.main.breakactivity

import com.varabyte.truthish.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.timer.main.domain.video.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class BreakActivityViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: BreakActivityViewModel


    private val testExercisesMenu = CurrentMenu(
        "Exercises",
        WorkoutVideosGateway.getWorkoutVideos().map { MenuItem("test", it.title) },
        type = Type.GRID
    )

    private val choseYourSpaceMenu = CurrentMenu(
        "Chose your space", listOf(
            MenuItem(id = "1.1.1.1", title = "Sitting", children = testExercisesMenu),
            MenuItem(id = "1.1.1.2", title = "Standing 1", children = testExercisesMenu),
            MenuItem(id = "1.1.1.2", title = "Standing 2", children = testExercisesMenu),
            MenuItem(id = "1.1.1.2", title = "Floor time", children = testExercisesMenu),
        )
    )


    private val energySelectionMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(
                id = "1.1.1",
                title = "Low energy?",
                subtitle = " 1 exercise for about one minute. Still Counts",
                choseYourSpaceMenu
            ),
            MenuItem(
                id = "1.1.2",
                title = "High Energy?",
                subtitle = "4  minutes of moderate to intense exercises",
                choseYourSpaceMenu
            ),
        )
    )
    private val moveYourBodyMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(id = "1.1", title = "Workouts", children = energySelectionMenu),
            MenuItem(id = "1.2", title = "Dance Break", children = energySelectionMenu),
            MenuItem(id = "1.3", title = "Shake it out", children = energySelectionMenu),
            MenuItem(id = "1.4", title = "Eye Stretch", children = energySelectionMenu)
        )
    )

    private val testRootMenu = CurrentMenu(
        menuItems = listOf(
            MenuItem(
                id = "1", title = "Move Your Body",
                children = moveYourBodyMenu,
            ),
            MenuItem(id = "2", title = "Fuel up"),
            MenuItem(id = "3", title = "Tidy up"),
            MenuItem(id = "4", title = "Clear your mind"),
            MenuItem(id = "5", title = "Mini challenge"),
        )
    )

    @BeforeTest
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        viewModel = BreakActivityViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should show root menu and no back button`() = runTest {
        val viewState = viewModel.viewState.first()
        assertThat(viewState.currentMenu.menuItems.map { it.id }).isEqualTo(testRootMenu.menuItems.map { it.id })
        assertThat(viewState.showBackButton).isFalse()
    }

    @Test
    fun `navigateTo move your body should show move your body menu`() = runTest {
        viewModel.navigateTo(testRootMenu.menuItems.first())
        val viewState = viewModel.viewState.first()
        assertThat(viewState.currentMenu).isEqualTo(moveYourBodyMenu)
        assertThat(viewState.showBackButton).isTrue()

    }

    @Test
    fun `navigateTo any item from move your body menu should show energy selection menu`() =
        runTest {
            moveYourBodyMenu.menuItems.forEachIndexed { index, menuItemToNavigate ->
                viewModel = BreakActivityViewModel()
                viewModel.navigateTo(menuItemToNavigate)
                val viewState = viewModel.viewState.first()
                assertThat(viewState.currentMenu).isEqualTo(energySelectionMenu)
                assertThat(viewState.showBackButton).isTrue()
            }
        }


    @Test
    fun `navigateTo any item from energy selection menu should show chose your space menu`() =
        runTest {
            energySelectionMenu.menuItems.forEachIndexed { index, menuItemToNavigate ->
                viewModel = BreakActivityViewModel()
                viewModel.navigateTo(menuItemToNavigate)
                val viewState = viewModel.viewState.first()
                assertThat(viewState.currentMenu).isEqualTo(choseYourSpaceMenu)
                assertThat(viewState.showBackButton).isTrue()
            }
        }

    @Test
    fun `navigateTo any item from chose your space  menu should show exersises menu`() =
        runTest {
            choseYourSpaceMenu.menuItems.forEachIndexed { index, menuItemToNavigate ->
                viewModel = BreakActivityViewModel()
                viewModel.navigateTo(menuItemToNavigate)
                val viewState = viewModel.viewState.first()
                assertThat(viewState.currentMenu.menuItems).isEqualTo(testExercisesMenu)
                assertThat(viewState.showBackButton).isTrue()
            }
        }
}


//    @Test
//    fun `navigateTo item with children should update currentMenu and show back button`() = runTest {
//        viewModel.navigateTo(rootParent.first())
//        val viewState = viewModel.viewState.first()
//
//        assertThat(viewState.currentMenu).isEqualTo(rootParent.first().children)
//        assertThat(viewState.showBackButton).isTrue()
//    }
//
//    @Test
//    fun `navigateTo item without children should not change menu or back button state significantly`() =
//        runTest {
//            val initialViewState = viewModel.viewState.first()
//            viewModel.navigateTo(childSimple)
//            val viewState = viewModel.viewState.first()
//
//            assertThat(viewState.currentMenu).isEqualTo(initialViewState.currentMenu)
//            assertThat(viewState.showBackButton).isEqualTo(initialViewState.showBackButton)
//
//            viewModel.navigateTo(rootParent.first())
//            viewModel.navigateTo(childSimple)
//            val viewStateAfterInternalNavigation = viewModel.viewState.first()
//
//            assertThat(viewStateAfterInternalNavigation.currentMenu).isEqualTo(rootParent.first().children)
//            assertThat(viewStateAfterInternalNavigation.showBackButton).isTrue()
//        }
//
//
//    @Test
//    fun `onBackClick from a child menu should return to parent menu`() = runTest {
//        viewModel.navigateTo(rootParent.first())
//        var viewState = viewModel.viewState.first()
//        assertThat(viewState.showBackButton).isTrue()
//        assertThat(viewState.currentMenu).isEqualTo(rootParent.first().children)
//
//        viewModel.onBackClick()
//        viewState = viewModel.viewState.first()
//
//        assertThat(viewState.currentMenu).isEqualTo(testRootMenu)
//        assertThat(viewState.showBackButton).isFalse()
//    }
//
//    @Test
//    fun `onBackClick from root menu should do nothing`() = runTest {
//        val initialViewState = viewModel.viewState.first()
//        viewModel.onBackClick()
//        val finalViewState = viewModel.viewState.first()
//
//        assertThat(finalViewState.currentMenu).isEqualTo(initialViewState.currentMenu)
//        assertThat(finalViewState.showBackButton).isFalse()
//    }
//
//    @Test
//    fun `onBackClick multiple times should navigate up the stack correctly`() = runTest {
//        viewModel.navigateTo(rootParent.first())
//        viewModel.navigateTo(childWithChildren)
//        var viewState = viewModel.viewState.first()
//
//        assertThat(viewState.currentMenu).isEqualTo(childWithChildren.children)
//        assertThat(viewState.showBackButton).isTrue()
//
//        viewModel.onBackClick()
//        viewState = viewModel.viewState.first()
//        assertThat(viewState.currentMenu).isEqualTo(rootParent.first().children)
//        assertThat(viewState.showBackButton).isTrue()
//
//        viewModel.onBackClick()
//        viewState = viewModel.viewState.first()
//        assertThat(viewState.currentMenu).isEqualTo(testRootMenu)
//        assertThat(viewState.showBackButton).isFalse()
//    }
//
//    @Test
//    fun `navigateTo deepest item without children should keep parent's children in currentMenu`() =
//        runTest {
//            viewModel.navigateTo(rootParent.first())
//            viewModel.navigateTo(childWithChildren)
//            viewModel.navigateTo(grandChildItem)
//
//            val viewState = viewModel.viewState.first()
//
//            assertThat(viewState.currentMenu).isEqualTo(childWithChildren.children)
//            assertThat(viewState.showBackButton).isTrue()
//        }
//
//    @Test
//    fun `navigateBackFromDeepestLevel should correctly go up the stack`() = runTest {
//        viewModel.navigateTo(rootParent.first())
//        viewModel.navigateTo(childWithChildren)
//        viewModel.navigateTo(grandChildItem)
//
//        viewModel.onBackClick()
//        var viewState = viewModel.viewState.first()
//        assertThat(viewState.currentMenu).isEqualTo(rootParent.first().children)
//        assertThat(viewState.showBackButton).isTrue()
//
//        viewModel.onBackClick()
//        viewState = viewModel.viewState.first()
//        assertThat(viewState.currentMenu).isEqualTo(testRootMenu)
//        assertThat(viewState.showBackButton).isFalse()
//    }
