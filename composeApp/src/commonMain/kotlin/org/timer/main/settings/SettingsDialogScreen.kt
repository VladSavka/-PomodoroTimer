package org.timer.main.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import org.koin.compose.viewmodel.*
import org.timer.main.*

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
        val keyboard = LocalSoftwareKeyboardController.current
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            RadioItem(
                radioOptions,
                viewModel,
                viewState,
                isSmallScreen
            )
            TextButton(enabled = viewState.isConfirmEnabled, onClick = {
                keyboard?.hide()
                isDialogVisible(false)
                onItemSelected()
            }) {
                Text(
                    text = "Confirm", color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    } else {
        AlertDialog(
            title = { Text(text = title) },
            text = {
                RadioItem(
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
                TextButton(enabled = viewState.isConfirmEnabled, onClick = {
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
fun RadioItem(
    items: List<String>,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState,
    isSmallScreen: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, text ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(48.dp)
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
                Column {
                    Column(modifier = Modifier.width(150.dp).padding(8.dp)) {
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
                    Column(modifier = Modifier.width(150.dp).padding(8.dp)) {
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
                    Column(modifier = Modifier.width(150.dp).padding(8.dp)) {
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
    RadioButton(
        selected = (index == selectedItemIndex),
        onClick = null
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = text,
        color = if (isSmallScreen) MaterialTheme.colorScheme.onPrimary else Color.Unspecified
    )
}