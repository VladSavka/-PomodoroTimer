package org.timer.main.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diamondedge.logging.logging

@ExperimentalMaterial3Api
@Composable
fun TimerScreen(
    modifier: Modifier,
    viewState: TimerViewState,
    viewModel: TimerViewModel,
) {
    Column(modifier) {
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Welcome to Kittydoro timer",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        val titles = listOf("Kittydoro", "Short Break", "Long Break")
        val pagerState = rememberPagerState(pageCount = { titles.size })

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.settledPage }.collect { page ->
                logging().debug { "Page changed to $page" }
                viewModel.onPageChanged(page)
            }
        }
        LaunchedEffect(viewState.selectedTabIndex) {
            pagerState.animateScrollToPage(viewState.selectedTabIndex)
        }

        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                SecondaryTabRow(
                    //  containerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTabIndex = viewState.selectedTabIndex
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = viewState.selectedTabIndex == index,
                            onClick = { viewModel.onPageChanged(index) },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }

                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        when (viewState.selectedTabIndex) {
                            0 -> KittidoroContent(viewState, viewModel)
                            1 -> ShortBreakContent(viewState, viewModel)
                            2 -> LongBreakContent(viewState, viewModel)
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "Kittydoros: ${viewState.kittyDoroNumber - 1}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
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
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = viewState.pomodoroTime,
            fontSize = 30.sp,
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
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp),
        )
        Text(
            text = viewState.shortBreakTime,
            fontSize = 30.sp,
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
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp),
        )
        Text(
            text = viewState.longBreakTime,
            fontSize = 30.sp,
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