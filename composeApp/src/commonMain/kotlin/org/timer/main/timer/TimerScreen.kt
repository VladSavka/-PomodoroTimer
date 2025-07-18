package org.timer.main.timer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import kotlinx.coroutines.*
import org.koin.compose.viewmodel.*
import org.timer.main.*
import org.timer.main.projects.*


@ExperimentalMaterial3Api
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = koinViewModel(),
    windowInfo: WindowInfo = remeberWindowInfo(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    AskNotificationPermission()
    if (windowInfo.isSmallScreen()) {
        ProjectsScreen(timerViewModel = viewModel)
    } else {
        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
            TimerTitle()
            Spacer(modifier = Modifier.height(if (windowInfo.isSmallScreen()) 48.dp else 16.dp))
            TimerPager(viewModel, viewState)
        }

    }
}

@Composable
expect fun AskNotificationPermission()


@Composable
fun TimerTitle() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "TomatoPaws timer",
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    )
}


@ExperimentalMaterial3Api
@Composable
fun TimerPager(
    viewModel: TimerViewModel,
    viewState: TimerViewState
) {
    val titles = listOf("Kittydoro", "Short Break", "Long Break")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.targetPage }.collect { page ->
            viewModel.onPageChanged(page)
        }
    }
    LaunchedEffect(viewState) {
        pagerState.animateScrollToPage(viewState.selectedTabIndex)
    }

    Row() {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        ) {
            SecondaryTabRow(
                selectedTabIndex = viewState.selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = viewState.selectedTabIndex == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }

            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    when (page) {
                        0 -> KittidoroContent(viewState, viewModel)
                        1 -> ShortBreakContent(viewState, viewModel)
                        2 -> LongBreakContent(viewState, viewModel)
                    }
                }
            }
            Text(
                text = "Kittydoros: ${viewState.kittyDoroNumber}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun KittidoroContent(
    viewState: TimerViewState,
    viewModel: TimerViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Keep the eye on the ball!",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = viewState.pomodoroTime,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Row {
            KittydoroStartPauseButton(viewState, viewModel)
            Button(
                onClick = { viewModel.onResetClick() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun KittydoroStartPauseButton(
    viewState: TimerViewState,
    viewModel: TimerViewModel,
    lazyListState: LazyListState? = null,
) {
    val scope = rememberCoroutineScope()

    if (viewState.isPomodoroStartVisible) {
        Button(
            onClick = {
                viewModel.onPomodoroStartClick()
                scrollToBeginningOfList(lazyListState, scope)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Start")
        }
    }

    if (viewState.isPomodoroPauseVisible) {
        Button(
            onClick = { viewModel.onPomodoroPauseClick() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Pause")
        }
    }
}

private fun scrollToBeginningOfList(
    lazyListState: LazyListState?,
    scope: CoroutineScope
) {
    lazyListState?.let { state ->
        scope.launch {
            state.animateScrollToItem(index = 0)
        }
    }
}

@Composable
private fun ShortBreakContent(viewState: TimerViewState, viewModel: TimerViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Take a break Kitty",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp),
        )
        Text(
            text = viewState.shortBreakTime,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Row {
            ShortBreakStartPauseButton(viewState, viewModel)
            Button(
                onClick = { viewModel.onResetClick() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun ShortBreakStartPauseButton(
    viewState: TimerViewState,
    viewModel: TimerViewModel,
    lazyListState: LazyListState? = null,
) {
    val scope = rememberCoroutineScope()
    if (viewState.isShortBreakStartVisible) {
        Button(
            onClick = {
                viewModel.onShortBreakStartClick()
                scrollToBeginningOfList(lazyListState, scope)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Start")
        }
    }

    if (viewState.isShortBreakPauseVisible) {
        Button(
            onClick = { viewModel.onShortBreakPauseClick() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Pause")
        }
    }
}


@Composable
fun LongBreakContent(viewState: TimerViewState, viewModel: TimerViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Take a long break Kitty",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp),
        )
        Text(
            text = viewState.longBreakTime,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Row {
            LongBreakStartPauseButton(viewState, viewModel)
            Button(
                onClick = { viewModel.onResetClick() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun LongBreakStartPauseButton(
    viewState: TimerViewState,
    viewModel: TimerViewModel,
    lazyListState: LazyListState? = null,
) {
    val scope = rememberCoroutineScope()

    if (viewState.isLongBreakStartVisible) {
        Button(
            onClick = {
                viewModel.onLongBreakStartClick()
                scrollToBeginningOfList(lazyListState, scope)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Start")
        }
    }

    if (viewState.isLongBreakPauseVisible) {
        Button(
            onClick = { viewModel.onLongBreakPauseClick() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Pause")
        }
    }
}


