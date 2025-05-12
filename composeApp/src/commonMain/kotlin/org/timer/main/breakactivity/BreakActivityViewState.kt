package org.timer.main.breakactivity

data class BreakActivityViewState(
    val selectedActivityId: String = "",
    val showActivityList: Boolean = true,
    var selectedItem: Item? = null,
    var showBackButton: Boolean = false
)