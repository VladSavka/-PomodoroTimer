import SwiftUI
import FirebaseMessaging
import ComposeApp

@main
struct iOSApp: App {
    
    let liveActivityBridge = GeneratedLiveActivityBridge.shared 
    
    init() {
        print("iOSApp init: Setting up Kotlin lambdas.")

        MobileAlarmKt.startLiveActivity = { title, isBreak, timeLeftInMillis, soundFileName in
            GeneratedLiveActivityBridge.shared.startActivity(
                categoryName: title,
                isBreak: isBreak.boolValue,
                totalDurationMillis: timeLeftInMillis.int64Value,
                soundFileName: soundFileName
            )
        }
   
        MobileAlarmKt.cancelLiveActivity = {
            GeneratedLiveActivityBridge.shared.endActivityByUserCancel()
        }
    }
        
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
