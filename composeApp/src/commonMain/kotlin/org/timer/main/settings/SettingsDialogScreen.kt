package org.timer.main.settings

import androidx.compose.foundation.*
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
        viewModel,
        viewState
    )
}

@Composable
fun SingleChoiceDialog(
    title: String,
    radioOptions: List<String>,
    isDialogVisible: (Boolean) -> Unit,
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
                isDialogVisible,
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
                    isSmallScreen,
                    isDialogVisible
                )
            },
            onDismissRequest = {
                isDialogVisible(false)
            },
            confirmButton = {
                TextButton(onClick = {
                    isDialogVisible(false)
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
    isDialogVisible: (Boolean) -> Unit,
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
                .padding(end = 8.dp),
        ) {
            TimerSettingsContent(items, viewModel, viewState, isSmallScreen)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Adjust padding as needed
            AlarmSoundContent(viewState, viewModel, isSmallScreen)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Adjust padding as needed
            SignOutButton(isDialogVisible, viewModel)
        }
    }
}

@Composable
fun SignOutButton(
    isDialogVisible: (Boolean) -> Unit,
    viewModel: SettingsViewModel
) {
    Button(
        onClick = {
            isDialogVisible(false)
            viewModel.onSignOutClick()
        },
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text("Sign Out")
    }
}


@Composable
private fun TimerSettingsContent(
    items: List<String>,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState,
    isSmallScreen: Boolean
) {
    val textFieldScale = 0.8f
    val rowSpacing = 0.dp
    val unscaledTextFieldHeight = 56.dp
    val unscaledErrorTextLineHeight = 16.sp.toDp()
    val buffer = 4.dp
    val maxRowHeight =
        (unscaledTextFieldHeight * textFieldScale) + (unscaledErrorTextLineHeight * textFieldScale) + buffer
    val minRowHeight =
        (unscaledTextFieldHeight * textFieldScale) // Let's try without the extra 2.dp first

    items.forEachIndexed { index, text ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(36.dp)
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            RadioButtonWithText(
                index = index,
                selectedItemIndex = viewState.selectedPresetPosition,
                text = text,
                isSmallScreen = isSmallScreen,
                onClick = { viewModel.onPresetClick(index) }
            )
        }

        if (index == items.size - 1) {
            MaterialTheme(if (isSmallScreen) darkScheme else lightScheme) {
                Column(
                    modifier = Modifier.padding(horizontal = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(rowSpacing) // This should be 0.dp
                ) {
                    SettingRow(
                        modifier = Modifier.heightIn(min = minRowHeight, max = maxRowHeight),
                        textFieldValue = viewState.pomodoroMinutes,
                        onTextFieldValueChange = { viewModel.updatePomodoroMinutes(it) },
                        label = "Focus time",
                        showError = viewState.showPomodoroError,
                        textFieldScale = textFieldScale,
                        isEnabled = viewState.selectedPresetPosition == items.size - 1
                    )

                    SettingRow(
                        modifier = Modifier.heightIn(min = minRowHeight, max = maxRowHeight),
                        textFieldValue = viewState.shortBreakMinutes,
                        onTextFieldValueChange = { viewModel.updateShortBreakMinutes(it) },
                        label = "Short break",
                        showError = viewState.showShortBreakError,
                        textFieldScale = textFieldScale,
                        isEnabled = viewState.selectedPresetPosition == items.size - 1
                    )

                    SettingRow(
                        modifier = Modifier.heightIn(min = minRowHeight, max = maxRowHeight),
                        textFieldValue = viewState.longBreakMinutes,
                        onTextFieldValueChange = { viewModel.updateLongBreakMinutes(it) },
                        label = "Long break",
                        showError = viewState.showLongBreakError,
                        textFieldScale = textFieldScale,
                        isEnabled = viewState.selectedPresetPosition == items.size - 1
                    )
                }
            }
        }
    }
}


@Composable
private fun SettingRow(
    modifier: Modifier = Modifier, // This modifier will carry the heightIn constraint
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    label: String,
    showError: Boolean,
    textFieldScale: Float,
    isEnabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .scale(textFieldScale)
                .width(150.dp),
            value = textFieldValue,
            onValueChange = {
                if (it.length <= 3 && (it.isEmpty() || it.isDigits())) {
                    onTextFieldValueChange(it)
                }
            },
            label = { Text(label) },
            singleLine = true,
            enabled = isEnabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showError,
            supportingText = null,
            trailingIcon = null,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        if (showError) {
            ErrorText()
        }
    }
}

@Composable
fun TextUnit.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

private fun String.isDigits() = matches(Regex("^\\d+\$"))

@Composable
private fun RadioButtonWithText(
    index: Int,
    selectedItemIndex: Int,
    text: String,
    isSmallScreen: Boolean,
    onClick: () -> Unit
) {
    Row(Modifier.clickable(onClick = onClick)) {
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
}

@Composable
private fun ErrorText(modifier: Modifier = Modifier, text: String = "Please enter a valid number") {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        modifier = modifier.padding(start = 4.dp)
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