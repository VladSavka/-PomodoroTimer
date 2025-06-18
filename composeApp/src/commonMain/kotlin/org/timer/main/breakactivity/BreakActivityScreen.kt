package org.timer.main.breakactivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
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
    timerViewModel: TimerViewModel = koinViewModel(),
    windowInfo: WindowInfo = remeberWindowInfo(),
    viewModel: BreakActivityViewModel = koinViewModel(),
) {

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val timerViewState by timerViewModel.viewState.collectAsStateWithLifecycle()
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
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
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
                        SubItem("3.2", "Do the dishes"),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!windowInfo.isSmallScreen()) {
                Spacer(Modifier.height(48.dp))
            }

            when (timerViewState.timerState) {
                is TimerState.ShortBreak -> {
                    Title(
                        text= "Short break",
                        showBackButton = viewState.showBackButton,
                        onBackClick = { viewModel.onBackClick() },
                        currentTime = timerViewState.shortBreakTime
                    )
                    if (windowInfo.isSmallScreen()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ToggleBreakTypeButton(
                            selectedBreakType = timerViewState.timerState,
                            isShortBreakSelected = {
                                if (it) timerViewModel.onShortBreakStartClick()
                                else timerViewModel.onLongBreakStartClick()
                            }
                        )
                    }
                }

                is TimerState.LongBreak -> {
                    viewModel.onActivitySelected("")
                    Title(
                        text = "Long break",
                        showBackButton = viewState.showBackButton,
                        onBackClick = { viewModel.onBackClick() },
                        currentTime = timerViewState.longBreakTime
                    )
                    if (windowInfo.isSmallScreen()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ToggleBreakTypeButton(
                            selectedBreakType = timerViewState.timerState,
                            isShortBreakSelected = {
                                if (it) timerViewModel.onShortBreakStartClick()
                                else timerViewModel.onLongBreakStartClick()
                            },
                        )
                    }
                }

                is TimerState.Pomodoro -> {
                    viewModel.onActivitySelected("")
                    if (windowInfo.isSmallScreen()) {
                        Title(
                            text = "Short break",
                            showBackButton = viewState.showBackButton,
                            onBackClick = { viewModel.onBackClick() },
                            currentTime = timerViewState.shortBreakTime
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ToggleBreakTypeButton(
                            selectedBreakType = timerViewState.timerState,
                            isShortBreakSelected = {
                                if (it) timerViewModel.onShortBreakStartClick()
                                else timerViewModel.onLongBreakStartClick()
                            }
                        )
                    }
                }
            }

            if (viewState.showActivityList && (timerViewState.timerState is TimerState.ShortBreak || (timerViewState.timerState is TimerState.Pomodoro && windowInfo.isSmallScreen()))) {
                Spacer(modifier = Modifier.height(8.dp))
                TwoLevelDeepList(
                    viewState = viewState,
                    items = items,
                    onFirstLevelItemClick = { viewModel.onFirstLevelItemClick(it) },
                    onItemSelected = {
                        viewModel.onActivitySelected(it)
                        timerViewModel.onShortBreakStartClick()
                    }
                )
            } else {
                val isAudioOrVideo = when (timerViewState.timerState) {
                    is TimerState.LongBreak -> false // LongBreakContent is not Audio/Video
                    else -> {
                        when (viewState.selectedActivityId) {
                            "1.1", "1.2", "1.3" -> true // Video
                            "2" -> true // Audio
                            else -> false // GoForIt or other content
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (!isAudioOrVideo) {
                        Spacer(Modifier.weight(0.4f))
                    }
                    when (timerViewState.timerState) {
                        is TimerState.LongBreak -> {
                            LongBreakContent(Modifier, timerViewState) // Will use biased spacing
                        }

                        else -> {
                            when (viewState.selectedActivityId) {
                                "1.1", "1.2", "1.3" -> Video(
                                    timerViewState,
                                    showDialog.value
                                ) // Will be centered by Arrangement.Center
                                "1.4" -> GoForIt(
                                    Modifier,
                                    timerViewState
                                ) // Will use biased spacing
                                "2" -> Audio(
                                    timerViewState,
                                    showDialog.value
                                ) // Will be centered by Arrangement.Center
                                "3.1", "3.2", "3.3", "3.4" -> GoForIt(
                                    Modifier,
                                    timerViewState
                                ) // Will use biased spacing
                                "4.1", "4.2", "4.3" -> GoForIt(
                                    Modifier,
                                    timerViewState
                                ) // Will use biased spacing
                                else -> {

                                }
                            }
                        }
                    }
                    if (!isAudioOrVideo) {
                        Spacer(Modifier.weight(0.6f))
                    }
                }
            }
        }
    }
}


@Composable
fun Title(
    text: String,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    currentTime: String,
) {
    val windowInfo = remeberWindowInfo()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (windowInfo.isSmallScreen()) 48.dp else 64.dp)
    ) {
        if (showBackButton) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if (windowInfo.isSmallScreen()) {
                Text(
                    text = currentTime, // Display the current time
                    fontSize = 18.sp, // Adjust font size as needed
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Slightly less prominent
                )
            }

        }

    }
}

@Composable
fun ToggleBreakTypeButton(
    selectedBreakType: TimerState,
    isShortBreakSelected: (Boolean) -> Unit,
) {
    var isLongSelected by remember(selectedBreakType) {
        mutableStateOf(
            when (selectedBreakType) {
                TimerState.LongBreak -> true
                is TimerState.Pomodoro -> false
                is TimerState.ShortBreak -> false
            }
        )
    }

    val buttonText = if (isLongSelected) "Short break" else "Long break"

    Button(
        onClick = {
            isLongSelected = !isLongSelected
            if (isLongSelected) {
                isShortBreakSelected(false)
            } else {
                isShortBreakSelected(true)
            }
        },
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Text(
            text = buttonText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Audio(viewState: TimerViewState, showDialog: Boolean) {
    if (viewState.timerState is TimerState.ShortBreak || viewState.timerState is TimerState.Pomodoro) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Show those moves! Dance like nobody watching",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
            val audioLink = when (viewState.timerState) {
                is TimerState.ShortBreak -> viewState.timerState.audioLink
                is TimerState.Pomodoro -> viewState.timerState.audioLink
                else -> ""
            }
            if (!showDialog) {
                VideoPlayer(
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp
                    ).fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    url = audioLink,
                )
            }
        }
    }
}

@Composable
fun Video(viewState: TimerViewState, showDialog: Boolean) {
    viewState.timerState.apply {
        if (this is TimerState.ShortBreak || this is TimerState.Pomodoro) {
            Column(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val videoLink = when (this@apply) {
                    is TimerState.ShortBreak -> this@apply.videoLink
                    is TimerState.Pomodoro -> this@apply.videoLink
                    else -> ""
                }
                if (!showDialog) {
                    VideoPlayer(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        url = videoLink,
                    )
                }
            }
        }
    }
}

@Composable
fun GoForIt(modifier: Modifier = Modifier, viewState: TimerViewState) {
    if (viewState.timerState is TimerState.ShortBreak || viewState.timerState is TimerState.Pomodoro) {
        Text(
            modifier = modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineMedium,
            text = "Go for it!",
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun LongBreakContent(modifier: Modifier, viewState: TimerViewState) {
    if (viewState.timerState is TimerState.LongBreak) {
        Text(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineMedium,
            text = "Have some food, check direct and stretch a bit, buddy, you deserve it!",
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun TwoLevelDeepList(
    viewState: BreakActivityViewState,
    items: List<Item>,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit,
    onFirstLevelItemClick: (Item) -> Unit
) {
    if (!viewState.showActivityList) {
        return
    }

    Column(modifier = modifier) {
        if (viewState.selectedItem == null) {
            // Level 1: Main list
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(items) { item ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        onClick = {
                            onFirstLevelItemClick.invoke(item)
                            if (item.subItems.isEmpty()) {
                                onItemSelected(item.id)
                            }
                        }
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = item.title,
                                    textAlign = TextAlign.Center
                                )
                            },
                            modifier = Modifier.padding(8.dp),
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    }
                }
            }
        } else {
            // Level 2: Sub-items list with "Back" button
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(viewState.selectedItem!!.subItems) { subItem ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        onClick = {
                            onItemSelected(subItem.id)
                        }
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = subItem.title,
                                    textAlign = TextAlign.Center
                                )
                            },
                            modifier = Modifier.padding(8.dp),
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    }
                }
            }
        }
    }
}


// Sample data structure (replace with your actual data)
data class Item(val id: String, val title: String, val subItems: List<SubItem> = emptyList())
data class SubItem(val id: String, val title: String)
