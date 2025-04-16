package org.timer.main.projects

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import org.koin.compose.viewmodel.*
import org.timer.main.data.entity.*

@Composable
fun ProjectsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = koinViewModel(),
) {

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            title = { Text(text = "Add Project") },
            text = {
                OutlinedTextField(
                    value = viewState.projectName,
                    onValueChange = { viewModel.onProjectTitleUpdate(it) },
                    label = { Text("Name") }
                )
            },
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.onAddProjectSubmitClick()
                    },
                    enabled = viewState.isSubmitEnabled
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
    Box(modifier) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            LazyColumn(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                items(viewState.projects) { task ->
                    ProjectItem(task) { id ->
                        viewModel.removeProject(id)
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                viewModel.onProjectTitleUpdate("")
                showDialog = true
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Add Project")
        }
    }
}

@Composable
fun ProjectItem(project: Project, onCloseClick: (Long) -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Header(project, { onCloseClick(project.id) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun Header(
    project: Project,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = project.title,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
                .weight(1f), // Use weight to take remaining space
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center, // Align text to the start
        )
        Box(modifier = Modifier) {
            IconButton(
                modifier = Modifier.size(20.dp).align(Alignment.TopEnd)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(1.dp)
                    .clip(CircleShape),

                onClick = onCloseClick
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }
    }
}