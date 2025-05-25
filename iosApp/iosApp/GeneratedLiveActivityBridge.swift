import Foundation
import ActivityKit
import Combine // Needed for ObservableObject

// Ensure your ActivityAttributes struct is defined, matching your widget's needs.
// This should be in a shared location if your widget extension also needs it,


@available(iOS 16.1, *) // Mark class available for iOS 16.1+ due to ActivityKit
class GeneratedLiveActivityBridge: ObservableObject { // Conform to ObservableObject for @StateObject if used, or just good practice
    
    // Singleton instance
    static let shared = GeneratedLiveActivityBridge()
    
    // Keep track of the currently active Live Activity started by this bridge
    private var currentActivity: Activity<TimerWidgetsAttributes>?
    
    // Private initializer to enforce singleton usage
    private init() {
        print("GeneratedLiveActivityBridge INITIALIZED (Singleton)")
        // You could potentially load any existing activities for this app here
        // if you needed to reconnect to them on app launch, though typically
        // you'd start fresh or rely on system state.
    }
    
    deinit {
        print("GeneratedLiveActivityBridge DEINITIALIZED")
        // No timer to invalidate here as we removed the internal dismissalTimer
    }
    
    // MARK: - Public Methods
    
    /// Starts a new Live Activity.
    /// Ends any existing Live Activity managed by this bridge before starting a new one.
    func startActivity(totalDurationMillis: Int64, categoryName: String) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else {
            print("Bridge: Live Activities are not enabled in system settings.")
            return
        }
        
        // End any existing activity before starting a new one to avoid duplicates from this app instance
        endExistingActivityImmediately()
        
        let now = Date()
        let totalDurationSeconds = TimeInterval(totalDurationMillis) / 1000.0
        
        guard totalDurationSeconds > 0 else {
            print("Bridge: Total duration must be positive. Cannot start activity.")
            return
        }
        let targetEndDate = now.addingTimeInterval(totalDurationSeconds)
        
        // Attributes for the Live Activity
        let attributes = TimerWidgetsAttributes(
            startDate: now,
            endDate: targetEndDate
        )
        
        // Initial content state for the Live Activity
        let initialContentState = TimerWidgetsAttributes.ContentState(categoryName: categoryName)
        
        // Stale date: When the system considers the Live Activity content outdated if not updated.
        // Usually a bit after the expected end time.
        let staleDate = targetEndDate.addingTimeInterval(5 * 60) // 5 minutes after it should have ended
        
        let activityContent = ActivityContent(state: initialContentState, staleDate: staleDate, relevanceScore: 100.0) // High relevance
        
        print("Bridge: Attempting to start Live Activity. Expected End: \(targetEndDate), Category: \(categoryName)")
        
        Task { // Perform ActivityKit operations in an async Task
            do {
                let activity = try Activity.request(
                    attributes: attributes,
                    content: activityContent,
                    pushType: nil // Use .token for remote push updates, nil for local only
                )
                self.currentActivity = activity // Store the new activity
                print("✅ Bridge: Live Activity started successfully. ID: \(activity.id)")
                
                // Start observing the state of this activity
                observeActivityState(activity: activity)
                
            } catch {
                print("❌ Bridge: An unexpected error occurred while starting Live Activity: \(error.localizedDescription)")
            }
        }
    }
    
    /// Ends the currently tracked Live Activity.
    /// This is typically called when the app determines the activity should end (e.g., timer finished, user action).
    func endCurrentLiveActivity(dismissalPolicy: ActivityUIDismissalPolicy = .default) {
        guard let activityToEnd = self.currentActivity else {
            print("Bridge: No currently tracked Live Activity to end.")
            // As a fallback, you could try to find *any* activity from this app,
            // but it's cleaner if currentActivity is reliably managed.
            if let firstAvailableActivity = Activity<TimerWidgetsAttributes>.activities.first {
                print("Bridge: No tracked activity, but found an existing one (\(firstAvailableActivity.id)). Ending it.")
                Task {
                    await firstAvailableActivity.end(nil, dismissalPolicy: dismissalPolicy)
                }
            }
            return
        }
        
        print("Bridge: Attempting to end Live Activity ID: \(activityToEnd.id) with policy: \(dismissalPolicy)")
        Task {
            await activityToEnd.end(nil, dismissalPolicy: dismissalPolicy)
            // The observation loop should set self.currentActivity = nil when it gets the .dismissed or .ended state.
            // However, you can also set it to nil here if you want to be more immediate,
            // but be mindful if the observation task is also trying to modify it.
            // For simplicity, we let the observer handle clearing self.currentActivity.
            print("Bridge: End request sent for activity ID: \(activityToEnd.id).")
        }
    }
    
    /// Ends the Live Activity due to a direct user cancellation from the app's UI.
    func endActivityByUserCancel() {
        print("Bridge: User initiated cancel. Ending Live Activity immediately.")
        endCurrentLiveActivity(dismissalPolicy: .immediate) // Use immediate dismissal for user actions
    }
    
    // MARK: - Private Helper Methods
    
    /// Ends any existing Live Activity that this bridge instance is currently tracking.
    private func endExistingActivityImmediately() {
        if let activity = self.currentActivity {
            print("Bridge: Ending existing tracked Live Activity (ID: \(activity.id)) before starting a new one.")
            Task {
                await activity.end(nil, dismissalPolicy: .immediate) // End immediately
                // self.currentActivity will be set to nil by the observer or if the end is synchronous.
            }
        }
        // Consider also iterating Activity<TimerWidgetsAttributes>.activities if there's a chance
        // currentActivity became nil but an activity from this app still exists.
        // For most simple cases, managing self.currentActivity is sufficient.
    }
    
    /// Observes the state changes of a Live Activity.
    private func observeActivityState(activity: Activity<TimerWidgetsAttributes>) {
        Task {
            for await stateUpdate in activity.activityStateUpdates {
                print("ℹ️ Bridge: Activity \(activity.id) state changed to: \(stateUpdate)")
                if stateUpdate == .dismissed || stateUpdate == .ended {
                    // If the activity being observed is the one we are currently tracking, clear our reference.
                    if self.currentActivity?.id == activity.id {
                        print("ℹ️ Bridge: Monitored activity \(activity.id) is now \(stateUpdate). Clearing local reference.")
                        self.currentActivity = nil
                    } else {
                        print("ℹ️ Bridge: Activity \(activity.id) (not current) is now \(stateUpdate).")
                    }
                }
            }
            // This point is reached when the observation stream naturally concludes
            // (e.g., activity is definitively finished and no more updates will come).
            print("ℹ️ Bridge: Observation loop finished for activity \(activity.id). It's likely permanently ended/dismissed.")
            // Ensure currentActivity is nil if this was the one being tracked and somehow wasn't cleared.
            if self.currentActivity?.id == activity.id {
                self.currentActivity = nil
            }
        }
    }
}
    // You could add an update method here if your Live Activity's ContentState needs to change
    // func updateLiveActivity(newCategoryName: String) {
    //     guard let activity
