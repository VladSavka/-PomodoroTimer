import UIKit
import UserNotifications // Import UserNotifications

class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate { // Conform to UNUserNotificationCenterDelegate

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        UNUserNotificationCenter.current().delegate = self
        
        
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
        GeneratedLiveActivityBridge.shared.endCurrentLiveActivity(dismissalPolicy: .immediate)

        completionHandler([.banner, .sound, .badge])

    }

    // Optional: Handle user tapping on the notification (foreground or background)
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        
        let userInfo = response.notification.request.content.userInfo
        let alarmId = userInfo["alarm_id"] as? String ?? "unknown"

        print("iOS Notification Tapped: ID - \(alarmId)")
        // Handle the action, e.g., navigate to a specific screen based on alarmId
        GeneratedLiveActivityBridge.shared.endCurrentLiveActivity(dismissalPolicy: .immediate)
        completionHandler()
    }

}
