package org.timer.main.timer

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import dev.icerock.moko.permissions.*
import dev.icerock.moko.permissions.compose.*
import dev.icerock.moko.permissions.notifications.*
import kotlinx.coroutines.launch

private fun canScheduleExactAlarms(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        return alarmManager?.canScheduleExactAlarms() ?: false
    }
    return true
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // It's good practice to check if an activity can handle the intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Log or handle the case where the settings screen cannot be opened
            }
        }
    }
}

@Composable
actual fun AskNotificationPermission() {
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller: PermissionsController =
        remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)

    var permissionState by remember { mutableStateOf(PermissionState.NotDetermined) }
    val coroutineScope = rememberCoroutineScope()

    val refreshPermissionState: () -> Unit = {
        coroutineScope.launch {
            permissionState = controller.getPermissionState(Permission.REMOTE_NOTIFICATION)
        }
    }

    LaunchedEffect(Unit) {
        refreshPermissionState()
    }

    LaunchedEffect(permissionState) {
        when (permissionState) {
            PermissionState.NotDetermined, PermissionState.Denied -> {
                try {
                    controller.providePermission(Permission.REMOTE_NOTIFICATION)
                    refreshPermissionState()
                } catch (deniedException: DeniedException) {
                    refreshPermissionState()
                } catch (deniedAlwaysException: DeniedAlwaysException) {
                    refreshPermissionState()
                }
            }
            else -> { /* Granted or DeniedAlways (after request) handled by other effect */ }
        }
    }

    val context = LocalContext.current
    var showExactAlarmDialog by remember { mutableStateOf(false) }
    var exactAlarmCheckAttemptedForCurrentGrant by remember { mutableStateOf(false) }

    LaunchedEffect(permissionState) {
        if (permissionState == PermissionState.Granted) {
            if (!exactAlarmCheckAttemptedForCurrentGrant) {
                exactAlarmCheckAttemptedForCurrentGrant = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms(context)) {
                    showExactAlarmDialog = true
                }
            }
        } else { // Covers NotDetermined, Denied, DeniedAlways
            exactAlarmCheckAttemptedForCurrentGrant = false
        }
    }

    if (showExactAlarmDialog) {
        AlertDialog(
            onDismissRequest = {
                showExactAlarmDialog = false
            },
            title = { Text("Schedule Exact Alarms") },
            text = { Text("To ensure timely timer notifications, this app needs permission to schedule exact alarms. Please grant this permission in the system settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmDialog = false
                    openExactAlarmSettings(context)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}