import UIKit
import UserNotifications // Import UserNotifications

// If using SwiftUI App Lifecycle, ensure this AppDelegate is connected
// in your @main App struct using @UIApplicationDelegateAdaptor(AppDelegate.self)

class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate { // Conform to UNUserNotificationCenterDelegate

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        // Set the notification center delegate
        UNUserNotificationCenter.current().delegate = self
        
//        // Request notification authorization from the user
//        requestNotificationAuthorization()
        
        return true
    }

    // MARK: - UNUserNotificationCenterDelegate Methods

    // This method is called when a notification is delivered to a foreground app.
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        
        let userInfo = notification.request.content.userInfo
        let alarmId = userInfo["alarm_id"] as? String ?? "unknown"

        print("iOS Foreground Notification Received: ID - \(alarmId), Title - \(notification.request.content.title)")

        // To show the notification banner, play sound, and update badge:
        completionHandler([.banner, .sound, .badge])
        
        // If you want to handle it entirely in-app without a system banner:
        // completionHandler([])
        // Then you would trigger your custom in-app UI.
    }

    // Optional: Handle user tapping on the notification (foreground or background)
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        
        let userInfo = response.notification.request.content.userInfo
        let alarmId = userInfo["alarm_id"] as? String ?? "unknown"

        print("iOS Notification Tapped: ID - \(alarmId)")
        // Handle the action, e.g., navigate to a specific screen based on alarmId
        
        completionHandler()
    }

    // MARK: - Helper for Notification Authorization
    func requestNotificationAuthorization() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                print("iOS Notification permission granted.")
            } else if let error = error {
                print("iOS Notification permission error: \(error.localizedDescription)")
            } else {
                print("iOS Notification permission denied.")
            }
        }
    }
    
    // If using SwiftUI App Lifecycle, you might not need the SceneDelegate methods here.
}
