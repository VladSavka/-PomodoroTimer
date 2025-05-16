package org.timer.main.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import org.jetbrains.compose.resources.*
import org.koin.compose.viewmodel.*
import org.timer.main.*
import org.timer.ui.theme.*
import pomodorotimer.composeapp.generated.resources.*

@Composable
fun SettingsDialogScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    isDialogVisible: (Boolean) -> Unit
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    SingleChoiceDialog(
        title = "Settings",
        radioOptions = listOf(
            "25min/5min/15min",
            "50min/10min/30min",
            "Custom time (minutes)"
        ),
        isDialogVisible = { isDialogVisible(it) },
        onItemSelected = { viewModel.onPresetConfirmed() },
        viewModel,
        viewState
    )
}

@Composable
fun SingleChoiceDialog(
    title: String,
    radioOptions: List<String>,
    isDialogVisible: (Boolean) -> Unit,
    onItemSelected: () -> Unit,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState
) {

    val isSmallScreen = remeberWindowInfo().isSmallScreen()
    if (isSmallScreen) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Content(
                radioOptions,
                viewModel,
                viewState,
                isSmallScreen,
                onItemSelected
            )

        }

    } else {
        AlertDialog(
            title = { Text(text = title) },
            text = {
                Content(
                    radioOptions,
                    viewModel,
                    viewState,
                    isSmallScreen
                )
            },
            onDismissRequest = {
                isDialogVisible(false)
            },
            dismissButton = {
                TextButton(onClick = { isDialogVisible(false) }) {
                    Text(text = "Dismiss")
                }
            },
            confirmButton = {
                TextButton(enabled = viewState.isWebConfirmEnabled, onClick = {
                    isDialogVisible(false)
                    onItemSelected()
                }) {
                    Text(text = "Confirm")
                }
            }
        )
    }

}

@Composable
fun Content(
    items: List<String>,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState,
    isSmallScreen: Boolean,
    onItemSelected: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Use a Box as the clickable container
    Box(
        modifier = Modifier
            .wrapContentSize() // Fill the available space
            // Add a clickable modifier to dismiss the keyboard
            .clickable(
                // Use an empty interactionSource to prevent ripple effect
                interactionSource = remember { MutableInteractionSource() },
                indication = null // No visual indication on click
            ) {
                // Clear focus and hide the keyboard when clicked outside text fields
                focusManager.clearFocus()
                keyboard?.hide()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState) // Use the created scrollState
                .padding(end = 8.dp) // Add padding to the end of the column
        ) {
            TimerSettingsContent(items, viewModel, viewState, isSmallScreen)
            if (isSmallScreen) {
                Button(enabled = viewState.isMobileConfirmEnabled, onClick = {
                    keyboard?.hide()
                    focusManager.clearFocus()
                    onItemSelected()
                }) {
                    Text(
                        text = "Confirm", color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Adjust padding as needed
            AlarmSoundContent(viewState, viewModel, isSmallScreen)
        }
    }
}
@Composable
private fun TimerSettingsContent(
    items: List<String>,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState,
    isSmallScreen: Boolean
) {
    items.forEachIndexed { index, text ->
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(36.dp).padding(horizontal = 8.dp)
                .fillMaxWidth()
                .clickable {
                    viewModel.onPresetClick(index)
                }) {
            RadioButtonWithText(
                index,
                viewState.selectedPresetPosition,
                text,
                isSmallScreen
            )
        }
        if (index == items.size - 1) {
            MaterialTheme(if (isSmallScreen) darkScheme else lightScheme) {
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Column(modifier = Modifier.width(150.dp)) {
                        OutlinedTextField(
                            value = viewState.pomodoroMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.isDigits()) viewModel.updateFocusMinutes(it)
                            },
                            label = { Text("Focus time") },
                            singleLine = true,
                            enabled = viewState.selectedPresetPosition == items.size - 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewState.showPomodoroError,
                            supportingText = {
                                if (viewState.showPomodoroError) {
                                    Text(text = "Please enter a valid number")
                                }
                            }
                        )
                    }
                    Column(modifier = Modifier.width(150.dp)) {
                        OutlinedTextField(
                            value = viewState.shortBreakMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.isDigits()) viewModel.updateShortBreakMinutes(
                                    it
                                )
                            },
                            label = { Text("Short break") },
                            singleLine = true,
                            enabled = viewState.selectedPresetPosition == items.size - 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewState.showShortBreakError,
                            supportingText = {
                                if (viewState.showShortBreakError) {
                                    Text(text = "Please enter a valid number")
                                }
                            },
                        )
                    }
                    Column(modifier = Modifier.width(150.dp)) {
                        OutlinedTextField(
                            value = viewState.longBreakMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.isDigits()) viewModel.updateLongBreakMinutes(
                                    it
                                )
                            },
                            label = { Text("Long break") },
                            singleLine = true,
                            enabled = viewState.selectedPresetPosition == items.size - 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewState.showLongBreakError,
                            supportingText = {
                                if (viewState.showLongBreakError) {
                                    Text(text = "Please enter a valid number")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun String.isDigits() = matches(Regex("^\\d+\$"))

@Composable
private fun RadioButtonWithText(
    index: Int,
    selectedItemIndex: Int,
    text: String,
    isSmallScreen: Boolean
) {
    MaterialTheme(colorScheme = if (isSmallScreen) darkScheme else lightScheme) {
        RadioButton(
            selected = (index == selectedItemIndex),
            onClick = null
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = text,
        color = if (isSmallScreen) MaterialTheme.colorScheme.onPrimary else Color.Unspecified
    )
}

@Composable
private fun AlarmSoundContent(
    viewState: SettingsViewState,
    viewModel: SettingsViewModel,
    isSmallScreen: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Alarm sound: ",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = if (isSmallScreen) MaterialTheme.colorScheme.onPrimary else Color.Unspecified

        )
        // Add the clickable sound icon here
        Icon(
            imageVector = vectorResource(Res.drawable.sound), // Use a sound-related icon
            contentDescription = "Play alarm sound preview",
            modifier = Modifier
                .size(24.dp) // Adjust size as needed
                .clip(CircleShape) // Clip the icon to a circle
                .border(
                    1.dp,
                    if (isSmallScreen) Color.White else Color.Black,
                    CircleShape
                ) // Add a round border
                .clickable {
                    viewModel.onPlayAlarmSoundPreviewClick()
                }
                .align(Alignment.CenterVertically) // Align icon vertically
                .padding(2.dp) // Add padding inside the circle,
            , tint = if (isSmallScreen) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp)) // Add space between icon and TextButton

        var expanded by remember { mutableStateOf(false) }
        val alarmSoundOptions = viewState.alarmSoundOptions
        val selectedAlarmSound =
            alarmSoundOptions[viewState.selectedAlarmPos] // Use val here as it's derived from state


        Box(modifier = Modifier.wrapContentHeight()) {
            MaterialTheme(colorScheme = darkScheme) {

                TextButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface // Adjust color as needed
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ), // Add padding
                    shape = MaterialTheme.shapes.small, // Apply a shape
                    border = BorderStroke(
                        1.dp,
                        if (isSmallScreen) Color.White else Color.Black
                    ) // Add a border
                ) {
                    MaterialTheme(colorScheme = if (isSmallScreen) lightScheme else darkScheme) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                selectedAlarmSound, color = MaterialTheme.colorScheme.onPrimary
                            ) // Display the selected sound

                            Spacer(Modifier.width(4.dp)) // Space between text and icon
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown, // Dropdown arrow icon
                                contentDescription = "Expand alarm sound options",
                                 tint = if (isSmallScreen) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                alarmSoundOptions.forEachIndexed { position, sound ->
                    DropdownMenuItem(
                        text = { Text(sound) },
                        onClick = {
                            // No need to update selectedAlarmSound here, it's derived from viewState
                            expanded = false
                            viewModel.onAlarmSoundClick(position)
                        }
                    )
                }
            }
        }
    }
}