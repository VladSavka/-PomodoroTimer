import Foundation
import FirebaseFunctions
import FirebaseMessaging

class LiveActivityScheduler { // Or wherever this logic resides

    lazy var functions = Functions.functions(region: "us-central1")
    
    func scheduleActivityUpdate(
        liveActivityPushToken: String,
        endTime: Date // The exact UTC time for the update
    ) {
        // 1. Get the FCM Device Token
        Messaging.messaging().token { fcmToken, error in
            if let error = error {
                print("🛑 Error fetching FCM registration token: \(error)")
                return
            }

            guard let deviceFCMToken = fcmToken else {
                print("🛑 FCM registration token is nil.")
                return
            }

            print("📱 Fetched FCM Token: \(deviceFCMToken)")
            self.callSchedulerFunction(liveActivityPushToken: liveActivityPushToken,
                                       deviceFCMToken: deviceFCMToken,
                                       endTime: endTime)
        }
    }

    private func callSchedulerFunction(
        liveActivityPushToken: String,
        deviceFCMToken: String,
        endTime: Date
    ) {
        let isoFormatter = ISO8601DateFormatter()
        isoFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        let updateTimeISO = isoFormatter.string(from: endTime)
        
        let requestData: [String: Any] = [
            "liveActivityPushToken": liveActivityPushToken,
            "updateTimeISO": updateTimeISO,
            "deviceFCMToken" : deviceFCMToken
        ]
        
        
        
        print("🚀 Scheduling Live Activity update with data: \(requestData)")
        
        functions.httpsCallable("scheduleLiveActivityUpdate").call(requestData) { result, error in
            if let error = error as NSError? {
                if error.domain == FunctionsErrorDomain {
                    let code = FunctionsErrorCode(rawValue: error.code)
                    let message = error.localizedDescription
                    let details = error.userInfo[FunctionsErrorDetailsKey]
                    print("🛑 LiveActivityScheduler: Error calling 'scheduleLiveActivityUpdate': Code=\(String(describing: code)), Message=\(message), Details=\(String(describing: details))")
                } else {
                    print("🛑 LiveActivityScheduler: Unknown error calling 'scheduleLiveActivityUpdate': \(error.localizedDescription)")
                }
                return
            }
            
            if let data = result?.data as? [String: Any] {
                print("✅ LiveActivityScheduler: 'scheduleLiveActivityUpdate' successful. Response data: \(data)")
                // Example: Accessing specific fields from the response
                // if let successMessage = data["message"] as? String {
                //     print("   Message from function: \(successMessage)")
                // }
            } else {
                print("⚠️ LiveActivityScheduler: 'scheduleLiveActivityUpdate' returned no data or unexpected data format. Result: \(String(describing: result?.data))")
            }
        }
    }
}
