import UIKit
import FirebaseCore
import UserNotifications
import FirebaseMessaging


class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
          options: authOptions,
          completionHandler: { _, _ in }
        )

        application.registerForRemoteNotifications()

        return true
    }



    // Receive displayed notifications for iOS 10 devices.
     func userNotificationCenter(_ center: UNUserNotificationCenter,
                                 willPresent notification: UNNotification) async
       -> UNNotificationPresentationOptions {
       let userInfo = notification.request.content.userInfo

       // With swizzling disabled you must let Messaging know about the message, for Analytics
       // Messaging.messaging().appDidReceiveMessage(userInfo)

       // ...

       // Print full message.
       print(userInfo)

       // Change this to your preferred presentation option
       return [[.alert, .sound]]
     }

     func userNotificationCenter(_ center: UNUserNotificationCenter,
                                 didReceive response: UNNotificationResponse) async {
       let userInfo = response.notification.request.content.userInfo

       // ...

       // With swizzling disabled you must let Messaging know about the message, for Analytics
       // Messaging.messaging().appDidReceiveMessage(userInfo)

       // Print full message.
       print(userInfo)
     }
  
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
          print("APNS device token received: \(deviceToken.map { String(format: "%02.2hhx", $0) }.joined())")
          Messaging.messaging().apnsToken = deviceToken // CRITICAL: Give the APNs token to FCM
        }

        func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
          print("Failed to register for APNS: \(error.localizedDescription)")
          // If this is called, FCM won't get an APNs token and will likely fail to get an FCM token.
        }
    
}

extension AppDelegate: MessagingDelegate {
  // [START refresh_token]
  func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
    print("Firebase registration token: \(String(describing: fcmToken))")

    let dataDict: [String: String] = ["token": fcmToken ?? ""]
    NotificationCenter.default.post(
      name: Notification.Name("FCMToken"),
      object: nil,
      userInfo: dataDict
    )
    // TODO: If necessary send token to application server.
    // Note: This callback is fired at each app startup and whenever a new token is generated.
  }

  // [END refresh_token]
}
