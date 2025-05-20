import SwiftUI
import ActivityKit
import WidgetKit


@available(iOS 16.1, *)
struct TimerLiveActivityWidget: Widget {
    let kind: String = "org.KittydoroTimer.TimerLiveActivityWidget" // Use your app's bundle ID prefix

    var body: some WidgetConfiguration {
        ActivityConfiguration(for: TimerActivityAttributes.self) { context in
            // This is the UI for the Lock Screen and banner presentations
            TimerLiveActivityView(context: context)
        } dynamicIsland: { context in
            // --- This is where you define the Dynamic Island presentation ---
            DynamicIsland {
                // Expanded Regions
                DynamicIslandExpandedRegion(.leading) {
                    HStack {
                        Image(systemName: "timer")
                            .foregroundColor(.white)
                        Text(context.state.timerName)
                            .font(.headline)
                            .foregroundColor(.white)
                    }
                }

                DynamicIslandExpandedRegion(.trailing) {
                    Text(context.state.endTime, style: .timer)
                        .font(.headline.monospacedDigit())
                        .foregroundColor(.orange) // Example color
                }

                DynamicIslandExpandedRegion(.center) {
                    // Optional: Content for the center region when expanded
                    // Text("Center Content")
                    EmptyView()
                }

                DynamicIslandExpandedRegion(.bottom) {
                    // Optional: More detailed content or controls when expanded
                    // Example: Button("Pause") { /* ... */ }
                    EmptyView()
                }
            } compactLeading: {
                Image(systemName: "timer")
                    .foregroundColor(.orange)
            } compactTrailing: {
                Text(context.state.endTime, style: .timer)
                    .font(.caption.monospacedDigit())
                    .foregroundColor(.orange)
            } minimal: {
                Image(systemName: "timer")
                    .foregroundColor(.orange)
            }
            // Optional: Customize tap behavior for compact/minimal views
            // .widgetURL(URL(string: "kittidoro://timer/\(context.attributes.timerId)")) // Example URL
            // .keylineTint(Color.red) // Optional tint for the keyline around compact/minimal
        }
    }
}

// If you use a WidgetBundle (recommended for future expansion)
@available(iOS 16.1, *)
struct TimerLiveActivityWidgetBundle: WidgetBundle {
    var body: some Widget {
        TimerLiveActivityWidget()
    }
}

