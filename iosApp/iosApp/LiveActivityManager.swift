import Foundation
import ActivityKit
import SwiftUI // Required if ContentState uses SwiftUI types, good to have for context

// Ensure TimerActivityAttributes is defined in your project (as shown previously)
// struct TimerActivityAttributes: ActivityAttributes { ... }

@objcMembers // Make this class and its members visible to Objective-C and thus Kotlin
class LiveActivityManager: NSObject {

    @objc(sharedInstance) // Make the shared instance accessible via Objective-C/Kotlin
    static let shared = LiveActivityManager()
    
    private var currentActivity: Any? // Store as Any to handle iOS versioning for Activity
                                      // Or use #available checks for specific Activity type

    override private init() { // Private init for singleton
        super.init()
    }

    @objc
       func startTimerActivity(totalTimeSeconds: Int64, timerName: String) {
           if #available(iOS 16.1, *) {
               guard ActivityAuthorizationInfo().areActivitiesEnabled else {
                   print("LiveActivityManager: Live Activities are not enabled by the user.")
                   return
               }

               endActivity()

               let attributes = TimerActivityAttributes()
               let endTime = Date().addingTimeInterval(TimeInterval(totalTimeSeconds))
               let contentState = TimerActivityAttributes.ContentState(endTime: endTime, timerName: timerName)

               do {
                   let activity = try Activity<TimerActivityAttributes>.request(
                       attributes: attributes,
                       contentState: contentState,
                       pushType: nil
                   )
                   self.currentActivity = activity
                   print("LiveActivityManager: Started Live Activity with ID \(activity.id) for \(timerName), ending at \(endTime)")
                   observeActivity(activity: activity)

               } catch { // Catch the generic Error type
                   print("LiveActivityManager: Error starting Live Activity: \(error.localizedDescription)")
                   // You can try to cast to more specific Foundation errors if needed,
                   // but ActivityKit itself doesn't provide a public ActivityError enum.
                   // For example, to see if it's an NSError with more details:
                   // if let nsError = error as NSError? {
                   //     print("LiveActivityManager: NSError Code: \(nsError.code)")
                   //     print("LiveActivityManager: NSError Domain: \(nsError.domain)")
                   //     print("LiveActivityManager: NSError UserInfo: \(nsError.userInfo)")
                   // }
               }
           } else {
               print("LiveActivityManager: Live Activities are not available on this iOS version.")
           }
       }

    @available(iOS 16.1, *)
    private func observeActivity(activity: Activity<TimerActivityAttributes>) {
        Task { // Asynchronous task to observe changes
            for await activityState in activity.activityStateUpdates {
                switch activityState {
                case .active:
                    print("LiveActivityManager: Activity \(activity.id) is active.")
                case .dismissed:
                    print("LiveActivityManager: Activity \(activity.id) was dismissed.")
                    // Handle dismissal, e.g., if the user manually ended it.
                    // You might want to clear currentActivity if it matches.
                    if let current = self.currentActivity as? Activity<TimerActivityAttributes>, current.id == activity.id {
                        self.currentActivity = nil
                    }
                case .ended:
                    print("LiveActivityManager: Activity \(activity.id) has ended.")
                    // Handle programmatic end (e.g., timer finished naturally).
                    // You might want to clear currentActivity if it matches.
                    if let current = self.currentActivity as? Activity<TimerActivityAttributes>, current.id == activity.id {
                        self.currentActivity = nil
                    }
                case .stale:
                    print("LiveActivityManager: Activity \(activity.id) stale")
                @unknown default:
                    print("LiveActivityManager: Activity \(activity.id) encountered an unknown state.")
                }
            }
        }
    }

    @objc
    func updateTimerActivity(newEndTime: Date, newTimerName: String) {
        if #available(iOS 16.1, *) {
            guard let activity = currentActivity as? Activity<TimerActivityAttributes> else {
                print("LiveActivityManager: No active Live Activity to update.")
                return
            }

            let updatedContentState = TimerActivityAttributes.ContentState(endTime: newEndTime, timerName: newTimerName)
            // let alertConfiguration: AlertConfiguration? = nil // Optional: for time-sensitive alerts

            Task { // Updates should be done asynchronously
                await activity.update(
                    using: updatedContentState
                    // alertConfiguration: alertConfiguration
                )
                print("LiveActivityManager: Updated Live Activity \(activity.id). New end time: \(newEndTime)")
            }
        }
    }

    @objc
    func endActivity() {
        if #available(iOS 16.1, *) {
            guard let activity = currentActivity as? Activity<TimerActivityAttributes> else {
                // print("LiveActivityManager: No active Live Activity to end.")
                return
            }

            Task { // Ending should be done asynchronously
                // Optional: Define final content state if needed when ending
                // let finalContentState = ...
                await activity.end( /* using: finalContentState, */ dismissalPolicy: .default)
                print("LiveActivityManager: Ended Live Activity \(activity.id).")
                self.currentActivity = nil
            }
        } else {
             self.currentActivity = nil // Clear it if not on supported OS
        }
    }

    // Example of how to get all current activities for your app (e.g., for cleanup on app start)
    @objc
    func listAllTimerActivities() {
        if #available(iOS 16.1, *) {
            Task {
                for activity in Activity<TimerActivityAttributes>.activities {
                    print("Found existing activity: ID \(activity.id), State: \(activity.activityState), EndTime: \(activity.contentState.endTime)")
                    // You could potentially re-assign to self.currentActivity if one matches a known timer
                }
            }
        }
    }
}
