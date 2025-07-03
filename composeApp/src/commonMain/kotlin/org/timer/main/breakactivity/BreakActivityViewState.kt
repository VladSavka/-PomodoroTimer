package org.timer.main.breakactivity

import org.timer.main.domain.video.*

data class BreakActivityViewState(
    val screenContent: ScreenContent = ScreenContent.Menu(CurrentMenu(menuItems = emptyList())),
    var showBackButton: Boolean = false
)

sealed class ScreenContent{
    data class Menu(val currentMenu: CurrentMenu) : ScreenContent()
    data class VideoContent(val video: Video) : ScreenContent()
    data class AudioContent(val audio: Video) : ScreenContent()
    data object GoForItContent : ScreenContent()
    data object ToDoContent : ScreenContent()

}