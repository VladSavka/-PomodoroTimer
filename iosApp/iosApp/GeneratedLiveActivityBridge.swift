import Foundation
import ActivityKit
import Combine
import SwiftUI

@available(iOS 16.2, *)
class GeneratedLiveActivityBridge: ObservableObject {

    static let shared = GeneratedLiveActivityBridge()

    @Published private(set) var currentActivity: Activity<TimerWidgetsAttributes>?
    @Published private(set) var currentActivityPushToken: String?

    private var activityObservationTask: Task<Void, Error>? = nil
    private var pushTokenObservationTask: Task<Void, Never>? = nil
    private let liveActivityScheduler = LiveActivityScheduler()
    private var currentActivityTargetEndDate: Date?

    deinit {
        activityObservationTask?.cancel()
        pushTokenObservationTask?.cancel()
    }

    func startActivity(categoryName: String, isBreak: Bool, totalDurationMillis: Int64) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else {
            return
        }

        endCurrentLiveActivity(dismissalPolicy: .immediate, clearInternalStateForNewActivity: true)

        let now = Date()
        let totalDurationSeconds = TimeInterval(totalDurationMillis) / 1000.0

        guard totalDurationSeconds > 0 else {
            return
        }

        let targetEndDate = now.addingTimeInterval(totalDurationSeconds)
        self.currentActivityTargetEndDate = targetEndDate
        let attributes = TimerWidgetsAttributes(
            startDate: now,
            endDate: targetEndDate,
            isBreak: isBreak
        )

        let initialContentState = TimerWidgetsAttributes.ContentState(
            displayText: categoryName,
            isFinished: false
        )

        let staleDate = Calendar.current.date(byAdding: .hour, value: 4, to: targetEndDate) ?? targetEndDate.addingTimeInterval(4 * 60 * 60)
        let relevanceScore = 100.0

        let activityContent = ActivityContent(
            state: initialContentState,
            staleDate: staleDate,
            relevanceScore: relevanceScore
        )

        Task {
            do {
                let activity = try Activity<TimerWidgetsAttributes>.request(
                    attributes: attributes,
                    content: activityContent,
                    pushType: .token
                )

                await MainActor.run {
                    self.currentActivity = activity
                    self.currentActivityPushToken = nil
                }
                
                observeActivityState(activity: activity)
                observePushToken(for: activity)

                // ACTION REQUIRED: Send targetEndDate and activity.id to your server.
                // The currentActivityPushToken will be sent when observePushToken gets it.

            } catch {
                await MainActor.run {
                    self.currentActivity = nil
                    self.currentActivityPushToken = nil
                    self.currentActivityTargetEndDate = nil

                }
            }
        }
    }

    private func observePushToken(for activity: Activity<TimerWidgetsAttributes>) {
        pushTokenObservationTask?.cancel()
        pushTokenObservationTask = Task {
            for await pushTokenData in activity.pushTokenUpdates {
                let pushTokenString = pushTokenData.map { String(format: "%02x", $0) }.joined()
                await MainActor.run {
                    self.currentActivityPushToken = pushTokenString
                }
                // ACTION REQUIRED: Send this pushTokenString to your server.
                print("--------------------------------------------------------------------")
                          print("✅ LIVE ACTIVITY APNs PUSH TOKEN: \(pushTokenString)")
                          print("--------------------------------------------------------------------")
                          print("(Activity ID: \(activity.id))")
                // --- INTEGRATION POINT ---
                          // Ensure we have a target end date for the current activity
                guard let targetEndDate = self.currentActivityTargetEndDate, self.currentActivity?.id == activity.id else {
                                   print("⚠️ Missing target end date or activity mismatch when trying to schedule update for token: \(pushTokenString)")
                                   continue // Skip scheduling if data is inconsistent
                               }


                          print("📲 Calling LiveActivityScheduler to schedule update for token: \(pushTokenString) at \(targetEndDate)")
                          // Call your scheduler
                          self.liveActivityScheduler.scheduleActivityUpdate(
                              liveActivityPushToken: pushTokenString,
                              endTime: targetEndDate
                          )
                        //   --- END INTEGRATION POINT ---
                
            }
        }
    }

    func endCurrentLiveActivity(dismissalPolicy: ActivityUIDismissalPolicy = .default, clearInternalStateForNewActivity: Bool = false) {
        if clearInternalStateForNewActivity {
            activityObservationTask?.cancel()
            pushTokenObservationTask?.cancel()
            activityObservationTask = nil
            pushTokenObservationTask = nil
            currentActivityTargetEndDate = nil
        }

        guard let activityToEnd = self.currentActivity else {
            if Activity<TimerWidgetsAttributes>.activities.isEmpty == false {
                Task {
                    for activity in Activity<TimerWidgetsAttributes>.activities {
                        await activity.end(nil, dismissalPolicy: dismissalPolicy)
                    }
                }
            }
            if clearInternalStateForNewActivity {
                 if Thread.isMainThread { self.currentActivity = nil; self.currentActivityPushToken = nil }
                 else { DispatchQueue.main.async { self.currentActivity = nil; self.currentActivityPushToken = nil } }
            }
            return
        }

        Task {
            await activityToEnd.end(nil, dismissalPolicy: dismissalPolicy)
        }
    }

    func endActivityByUserCancel() {
        endCurrentLiveActivity(dismissalPolicy: .immediate)
    }

    private func clearTrackedActivityReferences() {
        if Thread.isMainThread {
            self.currentActivity = nil
            self.currentActivityPushToken = nil
        } else {
            DispatchQueue.main.async {
                self.currentActivity = nil
                self.currentActivityPushToken = nil
            }
        }
        self.activityObservationTask?.cancel()
        self.pushTokenObservationTask?.cancel()
        self.activityObservationTask = nil
        self.pushTokenObservationTask = nil
        self.currentActivityTargetEndDate = nil

    }

    private func observeActivityState(activity: Activity<TimerWidgetsAttributes>) {
        activityObservationTask?.cancel()

        activityObservationTask = Task {
            for await stateUpdate in activity.activityStateUpdates {
                if stateUpdate == .dismissed || stateUpdate == .ended {
                    if self.currentActivity?.id == activity.id {
                        clearTrackedActivityReferences()
                    }
                    break
                }
            }
            if self.currentActivity?.id == activity.id && (activity.activityState == .dismissed || activity.activityState == .ended) {
                 clearTrackedActivityReferences()
            }
        }
    }
}
