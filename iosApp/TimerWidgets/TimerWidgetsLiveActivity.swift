import ActivityKit
import WidgetKit
import SwiftUI


public struct TimerWidgetsAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var categoryName: String
        var isPaused: Bool = false
        var remainingTimeWhenPausedFormatted: String? = nil
        var progressWhenPaused: Double? = nil // Value between 0.0 and 1.0
    }
    
    var startDate: Date // Original start of this segment
    var endDate: Date   // Original end of this segment (before any pause)
    // If you restart activities on resume, these might be the start/end of the *current* segment
}






struct TimerWidgetsLiveActivity: Widget {

    private func formatTimeInterval(_ interval: TimeInterval?) -> String {
        guard let interval = interval, interval > 0 else { return "00:00" }
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.minute, .second]
        formatter.unitsStyle = .positional
        formatter.zeroFormattingBehavior = .pad
        return formatter.string(from: interval) ?? "00:00"
    }

    var body: some WidgetConfiguration {
        ActivityConfiguration(for: TimerWidgetsAttributes.self) { context in
            VStack {
                HStack(spacing: 15) {
                    Image(systemName: "clock.fill")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(Color.blue)
                        .frame(width: 60, height: 60)

                    VStack(alignment: .leading, spacing: 8) {
                        Text(context.state.categoryName)
                            .font(.body)
                            .foregroundStyle(.white)
                            .bold()
                        
                        if context.state.isPaused {
                            Text(context.state.remainingTimeWhenPausedFormatted ?? "Paused")
                                .font(.title)
                                .foregroundStyle(.orange)
                                .bold()
                                .monospacedDigit()
                        } else {
                            Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                                .font(.title)
                                .foregroundStyle(.white)
                                .bold()
                                .monospacedDigit()
                        }
                        
                        if context.state.isPaused, let progress = context.state.progressWhenPaused {
                            ProgressView(value: progress, total: 1.0)
                                .progressViewStyle(LinearProgressViewStyle())
                                .tint(.orange)
                                .frame(height: 10)
                        } else {
                            ProgressView(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: false)
                                .progressViewStyle(LinearProgressViewStyle())
                                .tint(.white)
                                .frame(height: 10, alignment: .top)
                                .clipped()
                        }
                    }
                    Spacer()
                }
                .padding(.vertical, 15)
                .padding(.horizontal, 20)
            }
            .activityBackgroundTint(Color.black.opacity(0.7))
            .activitySystemActionForegroundColor(Color.white)

        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    Image(systemName: context.state.isPaused ? "pause.circle.fill" : "timer")
                        .font(.title2)
                        .foregroundColor(context.state.isPaused ? .orange : .cyan)
                }
                DynamicIslandExpandedRegion(.trailing) {
                    if context.state.isPaused {
                        Text(context.state.remainingTimeWhenPausedFormatted ?? "Paused")
                            .font(.title2.bold().monospacedDigit())
                            .foregroundColor(.orange)
                            .frame(width: 80, alignment: .trailing)
                    } else {
                        Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                            .font(.title2.bold().monospacedDigit())
                            .foregroundColor(.cyan)
                            .frame(width: 80)
                    }
                }
                DynamicIslandExpandedRegion(.center) {
                    Text(context.state.categoryName)
                        .font(.headline)
                        .lineLimit(1)
                        .foregroundColor(context.state.isPaused ? .orange : .primary)
                }
                DynamicIslandExpandedRegion(.bottom) {
                    if context.state.isPaused, let progress = context.state.progressWhenPaused {
                        ProgressView(value: progress, total: 1.0)
                            .progressViewStyle(LinearProgressViewStyle())
                            .tint(.orange)
                            .frame(height: 8, alignment: .top)
                            .clipped()
                    } else {
                        ProgressView(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: false)
                            .progressViewStyle(LinearProgressViewStyle())
                            .tint(.cyan)
                            .frame(height: 8, alignment: .top)
                            .clipped()
                    }
                }
            } compactLeading: {
                if context.state.isPaused {
                    Image(systemName: "pause.fill")
                        .foregroundColor(.orange)
                        .padding([.leading], 2)
                } else {
                    Image(systemName: "timer")
                        .foregroundColor(.cyan)
                        .padding([.leading], 2)
                }
            } compactTrailing: {
                if context.state.isPaused {
                    Text(context.state.remainingTimeWhenPausedFormatted ?? "Hold")
                        .font(.caption.monospacedDigit())
                        .foregroundColor(.orange)
                        .frame(width: 45)
                        .padding([.trailing], 2)
                } else {
                    Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                        .font(.caption.monospacedDigit())
                        .foregroundColor(.cyan)
                        .frame(width: 45)
                        .padding([.trailing], 2)
                }
            } minimal: {
                if context.state.isPaused {
                    Image(systemName: "pause").foregroundColor(.orange)
                } else {
                    Image(systemName: "timer.square").foregroundColor(.cyan)
                }
            }
            .widgetURL(URL(string: "kittidoro://openTimer"))
            .keylineTint(Color.cyan)
        }
    }
}
