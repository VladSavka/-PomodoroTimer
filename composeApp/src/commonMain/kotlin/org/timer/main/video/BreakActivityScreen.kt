package org.timer.main.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import org.koin.compose.viewmodel.*
import org.timer.main.*
import org.timer.main.settings.*
import org.timer.main.timer.*

@Composable
fun BreakActivityScreen(
    viewState: TimerViewState,
    windowInfo: WindowInfo = remeberWindowInfo(),
    breakActivityViewModel: BreakActivityViewModel = koinViewModel(),
) {

    val breakActivityViewState by breakActivityViewModel.viewState.collectAsStateWithLifecycle()
    val showDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!windowInfo.isSmallScreen()) {
            if (showDialog.value) {
                SettingsDialogScreen(isDialogVisible = { showDialog.value = it })
            }
            IconButton(
                onClick = { showDialog.value = true },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }

        val items = remember {
            listOf(
                Item(
                    "1",
                    "Physical activity",
                    listOf(
                        SubItem("1.1", "Posture"),
                        SubItem("1.2", "ABC"),
                        SubItem("1.3", "Straightening"),
                        SubItem("1.4", "Take a walk")
                    )
                ),
                Item(
                    "2",
                    "Silly dance"
                ),
                Item(
                    "3", "Home chores", listOf(
                        SubItem("3.1", "Take out trash"),
                        SubItem("3.2", "Вo the dishes,"),
                        SubItem("3.3", "Make THAT space tidy"),
                        SubItem("3.4", "Do the laundry")
                    )
                ),
                Item(
                    "4", "Take care of yourself",
                    listOf(
                        SubItem("4.1", "Drink water"),
                        SubItem("4.2", "Grab quick snack"),
                        SubItem("4.3", "Make a coffee"),
                    ),
                )
            )
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        ) {
            if (!windowInfo.isSmallScreen()) {
                Spacer(Modifier.height(48.dp))
            }
            if (viewState.selectedTabIndex == 1 || viewState.selectedTabIndex == 2) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Break time",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
            when (viewState.timerState) {
                is TimerState.ShortBreak -> {
                    TwoLevelDeepList(
                        viewState = breakActivityViewState,
                        items = items,
                        onItemSelected = {
                            breakActivityViewModel.onActivitySelected(it)
                        })
                }

                is TimerState.LongBreak -> {
                    breakActivityViewModel.onActivitySelected("")
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LongBreakContent(Modifier.align(Alignment.Center), viewState)
                    }
                }

                is TimerState.Pomodoro -> {
                    breakActivityViewModel.onActivitySelected("")
                }
            }

        }
        when (breakActivityViewState.selectedActivityId) {
            "1.1", "1.2", "1.3" -> Video(viewState, showDialog.value)
            "1.4" -> GoForIt(modifier = Modifier.align(Alignment.Center), viewState)
            "2" -> Audio(viewState, showDialog.value)
            "3.1", "3.2", "3.3", "3.4" -> GoForIt(
                modifier = Modifier.align(Alignment.Center),
                viewState
            )

            "4.1", "4.2", "4.3" -> GoForIt(modifier = Modifier.align(Alignment.Center), viewState)
        }
    }
}

@Composable
fun BoxScope.Audio(viewState: TimerViewState, showDialog: Boolean) {
    if (viewState.timerState is TimerState.ShortBreak) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 64.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            val windowInfo = remeberWindowInfo()
            if (!windowInfo.isSmallScreen()) {
                Spacer(Modifier.height(48.dp))
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Show those moves! Dance like nobody watching",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            if (!showDialog) {
                VideoPlayer(
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp
                    ).fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    url = viewState.timerState.audioLink,
                )
            }
        }
    }
}

@Composable
fun BoxScope.Video(viewState: TimerViewState, showDialog: Boolean) {
    if (viewState.timerState is TimerState.ShortBreak) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 64.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            if (!showDialog) {
                VideoPlayer(
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp
                    ).fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    url = viewState.timerState.videoLink,
                )
            }
        }
    }
}

@Composable
fun GoForIt(modifier: Modifier, viewState: TimerViewState) {
    if (viewState.timerState is TimerState.ShortBreak) {
        Text(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineMedium,
            text = "Go for it!"
        )
    }
}

@Composable
fun BoxScope.LongBreakContent(modifier: Modifier, viewState: TimerViewState) {
    if (viewState.timerState is TimerState.LongBreak) {
        Text(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineMedium,
            text = "Have some food, check direct and stretch a bit, buddy, you deserve it!"
        )
    }
}

@Composable
fun TwoLevelDeepList(
    viewState: BreakActivityViewState,
    items: List<Item>,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit
) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    if (!viewState.show) {
        return
    }

    Column(modifier = modifier) {
        if (selectedItem == null) {
            // Level 1: Main list
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(items) { item ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        onClick = {
                            selectedItem = item
                            if (item.subItems.isEmpty()) {
                                onItemSelected(item.id)
                            }
                        }
                    ) {
                        ListItem(
                            headlineContent = { Text(modifier = Modifier.fillMaxWidth(), text = item.title, textAlign = TextAlign.Center) },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        } else {
            // Level 2: Sub-items list with "Back" button
            Column {
                IconButton(
                    onClick = { selectedItem = null },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(selectedItem!!.subItems) { subItem ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            onClick = {
                                onItemSelected(subItem.id)
                            }
                        ) {
                            ListItem(
                                headlineContent = { Text(modifier = Modifier.fillMaxWidth(), text = subItem.title, textAlign = TextAlign.Center) },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// Sample data structure (replace with your actual data)
data class Item(val id: String, val title: String, val subItems: List<SubItem> = emptyList())
data class SubItem(val id: String, val title: String)
