package org.timer.main.timer

import androidx.compose.foundation.layout.*
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
import org.timer.main.breakactivity.*

@ExperimentalMaterial3Api
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = koinViewModel(),
    windowInfo: WindowInfo = remeberWindowInfo()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    Column(modifier) {
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Kittydoro timer",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

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

        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        ) {
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
        if (windowInfo.isSmallScreen()) {
            BreakActivityScreen(viewState = viewState)
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
            if (viewState.isPomodoroStartVisible) {
                Button(
                    onClick = { viewModel.onPomodoroStartClick() },
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
            if (viewState.isShortBreakStartVisible) {
                Button(
                    onClick = { viewModel.onShortBreakStartClick() },
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
private fun LongBreakContent(viewState: TimerViewState, viewModel: TimerViewModel) {
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
            if (viewState.isLongBreakStartVisible) {
                Button(
                    onClick = { viewModel.onLongBreakStartClick() },
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

            Button(
                onClick = { viewModel.onResetClick() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Reset")
            }
        }
    }
}