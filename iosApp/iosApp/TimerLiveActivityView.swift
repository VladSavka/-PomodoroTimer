import SwiftUICore
import WidgetKit


// MARK: - Live Activity View (Lock Screen & Banner)
@available(iOS 16.1, *) // Or higher if you use newer APIs like 16.2+ features
struct TimerLiveActivityView: View {
    // context provides access to the dynamic state (ContentState)
    // and static attributes (TimerActivityAttributes)
    let context: ActivityViewContext<TimerActivityAttributes>

    var body: some View {
        VStack(alignment: .center) { // Centered alignment for better look on Lock Screen
            Text(context.state.timerName) // Display the timer's name
                .font(.title2) // Slightly larger font for the name
                .fontWeight(.medium)
                .foregroundColor(.white) // Example: White text
                .padding(.bottom, 2)

            // Display the countdown timer
            Text(context.state.endTime, style: .timer)
                .font(.system(size: 40, weight: .bold)) // Larger, bold timer text
                .monospacedDigit() // Ensures digits don't jump around
                .foregroundColor(.yellow) // Example: Yellow timer text
                .minimumScaleFactor(0.7) // Allow text to shrink if space is limited
                .lineLimit(1)

            // Example: A simple status message or icon
            // HStack {
            //     Image(systemName: "hourglass.bottomhalf.fill")
            //         .foregroundColor(.white.opacity(0.8))
            //     Text("Counting down...")
            //         .font(.caption)
            //         .foregroundColor(.white.opacity(0.8))
            // }
            // .padding(.top, 2)
        }
        .padding(EdgeInsets(top: 20, leading: 20, bottom: 20, trailing: 20)) // Generous padding
        // These modifiers are for the entire Live Activity appearance
        .activityBackgroundTint(Color.indigo.opacity(0.8)) // Example background tint
        .activitySystemActionForegroundColor(Color.white) // For system elements like close button
    }
}
