package org.timer.main.domain.settings

import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import kotlin.js.Promise

external interface JsResult : JsAny {
    val success: Boolean
    val error: String?
}

@JsName("myAppJsFirebase")
external object MyAppJsFirebase {
    fun getTimeSettings(callback: (String?) -> Unit): String
    fun removeTimeSettingsListener(listenerId: String)

    fun setTimeSettings(timeSettingsJson: String): Promise<JsResult>

    fun getAlarmSound(callback: (String) -> Unit): String
    fun removeAlarmSoundListener(listenerId: String)

    fun setAlarmSound(soundName: String): Promise<JsResult>
}

actual class FirebaseSettingsGateway : SettingsGateway {

    actual override fun getTimeSettings(): Flow<TimeSettings> = callbackFlow {
        val listenerID = MyAppJsFirebase.getTimeSettings { json ->
            if (json == null) {
                val sent = trySend(TimeSettings.default())
                if (!sent.isSuccess) {
                    logging("FirebaseSettingsGateway").d { "trySend default failed: channel closed" }
                }
            } else {
                try {
                    val ts = Json.decodeFromString<TimeSettings>(json)
                    val sent = trySend(ts)
                    if (!sent.isSuccess) {
                        logging("FirebaseSettingsGateway").d { "trySend TimeSettings failed: channel closed" }
                    }
                    logging("FirebaseSettingsGateway").d { "getTimeSettings emitted: $ts" }
                } catch (e: Throwable) {
                    close(e)
                }
            }
        }
        logging("FirebaseSettingsGateway").d { "listenerID: $listenerID" }
        awaitClose {
            MyAppJsFirebase.removeTimeSettingsListener(listenerID)
            logging("FirebaseSettingsGateway").d { "Removed listenerID: $listenerID" }
        }
    }

    actual override suspend fun setTimeSettings(timeSettings: TimeSettings) {
        val json = Json.encodeToString(timeSettings)
        val result: JsResult = MyAppJsFirebase.setTimeSettings(json).await()
        if (!result.success) throw Exception("Failed to setTimeSettings: ${result.error}")
    }

    actual override fun getAlarmSound(): Flow<AlarmSound> = callbackFlow {
        val listenerId = MyAppJsFirebase.getAlarmSound { soundName ->
            val sound = try {
                AlarmSound.valueOf(soundName)
            } catch (_: Throwable) {
                AlarmSound.STANDARD
            }
            trySend(sound)
        }
        awaitClose {
            MyAppJsFirebase.removeAlarmSoundListener(listenerId)
        }
    }

    actual override suspend fun setAlarmSound(alarmSound: AlarmSound) {
        val result: JsResult = MyAppJsFirebase.setAlarmSound(alarmSound.name).await()
        if (!result.success) throw Exception("Failed to setAlarmSound: ${result.error}")
    }
}


fun TimeSettings.Companion.default() = TimeSettings(
    selectedPosition = 0,
    pomodoroTime = 25 * 60 * 1000L,
    shortBreakTime = 5 * 60 * 1000L,
    longBreakTime = 15 * 60 * 1000L
)
