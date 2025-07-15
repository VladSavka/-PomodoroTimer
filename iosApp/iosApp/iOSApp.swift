import SwiftUI
import FirebaseMessaging
import FirebaseAuth
import ComposeApp

@main
struct iOSApp: App {
    
    let liveActivityBridge = GeneratedLiveActivityBridge.shared 
    
    init() {
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
        
        DefaultAuthGateway_iosKt.signInWithGoogle = {  callback in
            FirebaseAuthBridge.shared.signInWithGoogle { isSucsess,error in
                _ = callback(KotlinBoolean(bool: isSucsess), error)
            }
        }
        
        DefaultAuthGateway_iosKt.signOut = {
            FirebaseAuthBridge.shared.signOut()
        }
        
        DefaultAuthGateway_iosKt.observeAuthState = { callback in
            FirebaseAuthBridge.shared.observeAuthState { isLoggedIn,id,email  in
                   let kotlinBool = KotlinBoolean(bool: isLoggedIn)
                   _ = callback(kotlinBool,id,email)
               }
        }
         
    }
        
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
