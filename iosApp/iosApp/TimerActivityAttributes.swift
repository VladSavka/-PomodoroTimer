import ActivityKit
import SwiftUI // For ContentState if it includes SwiftUI types

struct TimerActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic data: the time the countdown ends
        var endTime: Date
        var timerName: String // e.g., "Pomodoro" or "Break"
        // You can add more dynamic states here if needed, e.g., current phase
    }

    // Static data (if any) - for a simple timer, you might not need much static data
    // For example, an initial duration or a theme color could be static.
    // var initialDurationMinutes: Int
}
