package org.timer.main.breakactivity

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import org.kodein.emoji.compose.*
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
                        text = "Short break",
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

            if (timerViewState.timerState is TimerState.ShortBreak || (timerViewState.timerState is TimerState.Pomodoro && windowInfo.isSmallScreen())) {
                Spacer(modifier = Modifier.height(8.dp))
                HierarchicalMenu(
                    currentMenu = viewState.currentMenu,
                    onClick = { viewModel.navigateTo(it) },
                    onRandomWorkoutClick = {}
                )
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
fun HierarchicalMenu(
    currentMenu: CurrentMenu,
    modifier: Modifier = Modifier, // Keep this for external layout control if needed
    onClick: (MenuItem) -> Unit,
    onRandomWorkoutClick: () -> Unit
) {
    val cornerRadius = 12.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = currentMenu.title != null,
            content = {
                currentMenu.title?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )
                }
            }
        )

        when (currentMenu.type) {
            Type.LIST -> ListMenu(currentMenu, onClick)
            Type.GRID -> GridMenu(
                currentMenu = currentMenu,
                onClick = onClick,
                modifier = Modifier.padding(top = if (currentMenu.title != null) 0.dp else 8.dp),
                onRandomWorkoutClick = onRandomWorkoutClick,
            )
            Type.AUDIO -> AudioMenu(currentMenu, onClick)
        }
    }
}

@Composable
fun AudioMenu(
    currentMenu: CurrentMenu,
    onClick: (MenuItem) -> Unit
) {
    currentMenu.menuItems.forEach { item ->
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            onClick = {
                onClick(item)
            }
        ) {
            ListItem(
                headlineContent = {
                    WithPlatformEmoji(item.title) { text, inlineContent ->
                        Text(
                            text = text,
                            textAlign = TextAlign.Start, // Align to start for better layout with leading icon
                            style = MaterialTheme.typography.titleMedium,
                            inlineContent = inlineContent
                        )
                    }
                },
                supportingContent = item.subtitle?.let { subtitleText ->
                    {
                        Text(
                            text = subtitleText,
                            textAlign = TextAlign.Start, // Align to start
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                        )
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow, // Dummy icon for audio
                        contentDescription = "Audio track", // Decorative, or provide specific description
                        modifier = Modifier.size(40.dp), // Adjust size as needed
                        tint = MaterialTheme.colorScheme.primary // Example tint
                    )
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp // Slightly more vertical padding for items with icons
                ),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer, // Match card
                    headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    supportingColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    leadingIconColor = MaterialTheme.colorScheme.primary // Ensure icon color is set if needed
                )
            )
        }
    }
}

@Composable
fun ListMenu(
    currentMenu: CurrentMenu,
    onClick: (MenuItem) -> Unit
) {
    currentMenu.menuItems.forEach { item ->
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            onClick = {
                onClick(item)
            }
        ) {
            ListItem(
                headlineContent = {
                    WithPlatformEmoji(item.title) { text, inlineContent ->
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = text,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium, // Example style
                            inlineContent = inlineContent
                        )
                    }


                },
                // Add supportingContent for the subtitle
                supportingContent = item.subtitle?.let { subtitleText ->
                    { // This lambda is required by the supportingContent parameter
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = subtitleText,
                            textAlign = TextAlign.Center, // Or another alignment if you prefer
                            style = MaterialTheme.typography.bodyMedium, // Example style
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f) // Slightly less prominent
                        )
                    }
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp, // Standard padding for ListItem content
                    vertical = 8.dp
                ),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    supportingColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun GridMenu(
    currentMenu: CurrentMenu,
    onClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    onRandomWorkoutClick: () -> Unit
) {
    val numberOfColums = 2
    val columns = GridCells.Fixed(numberOfColums) // Or GridCells.Adaptive for responsiveness

    LazyVerticalGrid(
        columns = columns,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(
            span = {
                GridItemSpan(numberOfColums)
            }
        ) {
            Button(
                onClick = onRandomWorkoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Gimme random")
            }
        }
        items(currentMenu.menuItems, key = { it.id }) { menuItem ->
            GridItem(
                menuItem = menuItem,
                onClick = { onClick(menuItem) }
            )
        }
    }
}

@Composable
private fun GridItem(
    menuItem: MenuItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // DEBUG: Force a fixed height
            // .heightIn(min = 180.dp) // Comment out for now
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Column fills the card
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Picture Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Let it take available space for now

                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = menuItem.title,
                    modifier = Modifier.fillMaxSize(0.8f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Title
            Text(
                text = menuItem.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}