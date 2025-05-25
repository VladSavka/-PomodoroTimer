import Foundation
import ActivityKit
import Combine

@available(iOS 16.1, *)
class GeneratedLiveActivityBridge: ObservableObject {
    
    static let shared = GeneratedLiveActivityBridge()
    
    @Published private(set) var currentActivity: Activity<TimerWidgetsAttributes>?
    
    private var originalCategoryNameForResume: String?
    private var timeRemainingAtPause: TimeInterval?
    private var activityObservationTask: Task<Void, Error>? = nil

    private init() {
        print("GeneratedLiveActivityBridge INITIALIZED")
    }
    
    deinit {
        print("GeneratedLiveActivityBridge DEINITIALIZED")
        activityObservationTask?.cancel()
    }

    private func formatTimeInterval(_ interval: TimeInterval?) -> String {
        guard let interval = interval, interval > 0 else { return "00:00" }
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.minute, .second]
        formatter.unitsStyle = .positional
        formatter.zeroFormattingBehavior = .pad
        return formatter.string(from: interval) ?? "00:00"
    }
    
    func startActivity(totalDurationMillis: Int64, categoryName: String) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else {
            print("Bridge: Live Activities not enabled.")
            return
        }
        
        endCurrentLiveActivity(dismissalPolicy: .immediate, clearInternalState: false)
        
        let now = Date()
        let totalDurationSeconds = TimeInterval(totalDurationMillis) / 1000.0
        
        guard totalDurationSeconds > 0 else {
            print("Bridge: Total duration must be positive.")
            return
        }
        
        let targetEndDate = now.addingTimeInterval(totalDurationSeconds)
        
        self.originalCategoryNameForResume = categoryName
        self.timeRemainingAtPause = nil

        let attributes = TimerWidgetsAttributes(
            startDate: now,
            endDate: targetEndDate
        )
        
        let initialContentState = TimerWidgetsAttributes.ContentState(
            categoryName: categoryName,
            isPaused: false,
            remainingTimeWhenPausedFormatted: nil,
            progressWhenPaused: nil
        )
        
        let staleDate = targetEndDate.addingTimeInterval(5 * 60)
        let activityContent = ActivityContent(state: initialContentState, staleDate: staleDate, relevanceScore: 100.0)
        
        print("Bridge: Attempting to start Live Activity. End: \(targetEndDate), Category: \(categoryName)")
        
        Task {
            do {
                let activity = try Activity.request(
                    attributes: attributes,
                    content: activityContent,
                    pushType: nil
                )
                
                await MainActor.run {
                    self.currentActivity = activity
                }
                print("✅ Bridge: Live Activity started. ID: \(activity.id)")
                observeActivityState(activity: activity)
            } catch {
                print("❌ Bridge: Error starting Live Activity: \(error.localizedDescription)")
                await MainActor.run {
                    self.currentActivity = nil
                }
                self.originalCategoryNameForResume = nil
            }
        }
    }
    
    func pauseLiveActivity(totalDurationMillis: Int64) {
        guard let activity = self.currentActivity, !activity.content.state.isPaused else {
            // ...
            return
        }

        let now = Date()
        let remaining = activity.attributes.endDate.timeIntervalSince(now)
        self.timeRemainingAtPause = max(0, remaining)

        // Calculate progress
        let totalDuration = activity.attributes.endDate.timeIntervalSince(activity.attributes.startDate)
        var currentProgress: Double? = nil
        if totalDuration > 0 {
            let elapsedTime = max(0, now.timeIntervalSince(activity.attributes.startDate)) // Ensure not negative if clock skewed
            currentProgress = min(1.0, elapsedTime / totalDuration) // Cap at 1.0
        }

        let pausedState = TimerWidgetsAttributes.ContentState(
            categoryName: activity.content.state.categoryName,
            isPaused: true,
            remainingTimeWhenPausedFormatted: formatTimeInterval(self.timeRemainingAtPause),
            progressWhenPaused: currentProgress // Set the calculated progress
        )
        
        print("Bridge: Pausing activity ID \(activity.id). Remaining: \(self.timeRemainingAtPause ?? 0)s. Progress: \(currentProgress ?? -1)")
        Task {
            let newContent = ActivityContent(state: pausedState, staleDate: .distantFuture)
            await activity.update(newContent)
            print("Bridge: Activity \(activity.id) updated to paused state.")
        }
    }

    func resumeLiveActivity() {
        guard let pausedActivity = self.currentActivity,
              pausedActivity.content.state.isPaused,
              let timeRemaining = self.timeRemainingAtPause,
              let categoryName = self.originalCategoryNameForResume
        else {
            print("Bridge: No paused activity to resume or missing state.")
            return
        }

        print("Bridge: Resuming. Time remaining: \(timeRemaining)s for category: \(categoryName)")
        
        endCurrentLiveActivity(dismissalPolicy: .immediate, clearInternalState: false)
        
        let remainingMillis = Int64(timeRemaining * 1000.0)
        startActivity(totalDurationMillis: remainingMillis, categoryName: categoryName)
        print("Bridge: New activity started for resume.")
    }
    
    func endCurrentLiveActivity(dismissalPolicy: ActivityUIDismissalPolicy = .default, clearInternalState: Bool = true) {
        guard let activityToEnd = self.currentActivity else {
            print("Bridge: No currently tracked Live Activity to end.")
            if Activity<TimerWidgetsAttributes>.activities.isEmpty == false {
                print("Bridge: No tracked activity, but attempting to end all activities for this app type.")
                Task {
                    for activity in Activity<TimerWidgetsAttributes>.activities {
                        await activity.end(nil, dismissalPolicy: dismissalPolicy)
                    }
                }
            }
            if clearInternalState {
                 clearAllInternalState()
            }
            return
        }
        
        print("Bridge: Attempting to end Live Activity ID: \(activityToEnd.id) with policy: \(dismissalPolicy)")
        Task {
            await activityToEnd.end(nil, dismissalPolicy: dismissalPolicy)
            print("Bridge: End request sent for activity ID: \(activityToEnd.id).")
        }
        
        if clearInternalState {
            clearAllInternalState(clearCurrentActivity: false)
        }
    }
    
    func endActivityByUserCancel() {
        print("Bridge: User initiated cancel. Ending Live Activity immediately.")
        endCurrentLiveActivity(dismissalPolicy: .immediate, clearInternalState: true)
    }
    
    private func clearAllInternalState(clearCurrentActivity: Bool = true) {
        if clearCurrentActivity {
            if Thread.isMainThread {
                self.currentActivity = nil
            } else {
                DispatchQueue.main.async {
                    self.currentActivity = nil
                }
            }
        }
        self.originalCategoryNameForResume = nil
        self.timeRemainingAtPause = nil
        self.activityObservationTask?.cancel()
        self.activityObservationTask = nil
        print("Bridge: Internal state cleared.")
    }
    
    private func observeActivityState(activity: Activity<TimerWidgetsAttributes>) {
        activityObservationTask?.cancel()

        activityObservationTask = Task {
            for await stateUpdate in activity.activityStateUpdates {
                print("ℹ️ Bridge: Activity \(activity.id) state changed to: \(stateUpdate)")
                if stateUpdate == .dismissed || stateUpdate == .ended {
                    if self.currentActivity?.id == activity.id {
                        print("ℹ️ Bridge: Monitored activity \(activity.id) is now \(stateUpdate). Clearing all state.")
                        await MainActor.run {
                            self.clearAllInternalState(clearCurrentActivity: true)
                        }
                    } else {
                        print("ℹ️ Bridge: Activity \(activity.id) (not current) is now \(stateUpdate). This might be an old observation.")
                    }
                    // Once an activity is dismissed or ended, break the loop for this observation task.
                    break
                }
            }
            print("ℹ️ Bridge: Observation loop finished for activity \(activity.id).")
            // Final check to ensure state is cleared if this was the current activity
            if self.currentActivity?.id == activity.id && (activity.activityState == .dismissed || activity.activityState == .ended) {
                 await MainActor.run {
                    self.clearAllInternalState(clearCurrentActivity: true)
                }
            }
        }
    }
}
