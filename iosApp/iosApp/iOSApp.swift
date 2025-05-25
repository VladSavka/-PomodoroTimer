import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    let liveActivityBridge = GeneratedLiveActivityBridge.shared 
    
    init() {
        print("iOSApp init: Setting up Kotlin lambdas.")

        MobileAlarmKt.startLiveActivity = { title, timeLeftInMillis in
            GeneratedLiveActivityBridge.shared.startActivity(totalDurationMillis: timeLeftInMillis.int64Value, categoryName: title)
        }
        
        MobileAlarmKt.pauseLiveActivity = { timeLeftInMillis in
            GeneratedLiveActivityBridge.shared.pauseLiveActivity(totalDurationMillis: timeLeftInMillis.int64Value)
            
        }
        
        MobileAlarmKt.resumeLiveActivity = {
            GeneratedLiveActivityBridge.shared.resumeLiveActivity()
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
