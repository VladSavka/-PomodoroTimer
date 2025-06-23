package org.timer.main.breakactivity

data class BreakActivityViewState(
    val currentMenu: CurrentMenu = CurrentMenu(menuItems = emptyList()),
    var showBackButton: Boolean = false
)