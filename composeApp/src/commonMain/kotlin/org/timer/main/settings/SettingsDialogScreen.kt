package org.timer.main.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsDialogScreen(
    viewModel: SettingsViewModel = viewModel { SettingsViewModel() },
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
        indexOfDefault = viewState.selectedPresetPosition,
        isDialogVisible = { isDialogVisible(it) },
        onItemSelected = { viewModel.onPresetConfirmed(it) },
        viewModel,
        viewState
    )
}

@Composable
fun SingleChoiceDialog(
    title: String,
    radioOptions: List<String>,
    indexOfDefault: Int,
    isDialogVisible: (Boolean) -> Unit,
    onItemSelected: (Int) -> Unit,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState
) {
    val (selectedItemIndex, setSelectedItemIndex) = remember {
        mutableStateOf(indexOfDefault)
    }
    AlertDialog(
        title = { Text(text = title) },
        text = {
            RadioItem(radioOptions, selectedItemIndex, setSelectedItemIndex, viewModel, viewState)
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
                onItemSelected(selectedItemIndex)
            }) {
                Text(text = "Confirm")
            }
        }
    )
}

@Composable
fun RadioItem(
    items: List<String>,
    selectedItemIndex: Int,
    setIndexOfSelected: (Int) -> Unit,
    viewModel: SettingsViewModel,
    viewState: SettingsViewState
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, text ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    setIndexOfSelected(index)
                    viewModel.onPresetSelected(index)
                }) {
                RadioButtonWithText(index, selectedItemIndex, setIndexOfSelected, text)
            }
            if (index == items.size - 1) {
                Row {
                    Column(modifier = Modifier.width(150.dp).padding(8.dp)) {
                        OutlinedTextField(
                            value = viewState.focusMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.isDigits()) viewModel.updateFocusMinutes(it)
                            },
                            label = { Text("Focus time") },
                            singleLine = true,
                            enabled = selectedItemIndex == items.size - 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewState.showFocusError,
                            supportingText = {
                                if (viewState.showFocusError) {
                                    Text(text = "Please enter a valid number")
                                }
                            }
                        )
                    }
                    Column(modifier = Modifier.width(150.dp).padding(8.dp)) {
                        OutlinedTextField(
                            value = viewState.shortBreakMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.isDigits()) viewModel.updateShortBreakMinutes(it)
                            },
                            label = { Text("Short break") },
                            singleLine = true,
                            enabled = selectedItemIndex == items.size - 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewState.showShortBreakError,
                            supportingText = {
                                if (viewState.showShortBreakError) {
                                    Text(text = "Please enter a valid number")
                                }
                            }

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
                            enabled = selectedItemIndex == items.size - 1,
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
    setIndexOfSelected: (Int) -> Unit,
    text: String
) {
    RadioButton(
        selected = (index == selectedItemIndex),
        onClick = {
            setIndexOfSelected(index)
        }
    )
    Text(
        text = text
    )
}