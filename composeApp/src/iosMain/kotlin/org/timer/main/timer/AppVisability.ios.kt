package org.timer.main.timer
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState

actual fun isAppInForeground(): Boolean {
    // UIApplication.sharedApplication.applicationState can be:
    // - UIApplicationStateActive: The app is running in the foreground and is receiving events.
    // - UIApplicationStateInactive: The app is running in the foreground but is not receiving events
    //   (e.g., during a system alert, or when transitioning). Often still considered "foreground".
    // - UIApplicationStateBackground: The app is in the background.
    val currentState = UIApplication.sharedApplication.applicationState
    return currentState == UIApplicationState.UIApplicationStateActive || currentState == UIApplicationState.UIApplicationStateInactive
    // You can choose to only consider UIApplicationStateActive if you need a stricter definition
    // return currentState == UIApplicationState.UIApplicationStateActive
}