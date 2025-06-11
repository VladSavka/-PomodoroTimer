import ActivityKit
import WidgetKit
import SwiftUI


public struct TimerWidgetsAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var displayText: String
        var isFinished: Bool
    }
    
    var startDate: Date
    var endDate: Date
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
                        if context.state.isFinished {
                            Text(context.state.displayText) // Should be "Finished"
                                .font(.title) // Or your desired font for "Finished"
                                .foregroundStyle(.green) // Example: Green for finished
                                .bold()
                        } else {
                            Text(context.state.displayText)
                                .font(.body)
                                .foregroundStyle(.white)
                                .bold()
                            
                            Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                                .font(.title)
                                .foregroundStyle(.white)
                                .bold()
                                .monospacedDigit()
                            
                            
                            
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
                    Image("timer")
                        .font(.title2)
                        .foregroundColor(.cyan)
                }
                DynamicIslandExpandedRegion(.trailing) {
                    if context.state.isFinished {
                                           Text("🎉") // Example for finished state in compact island
                                               .font(.title2.bold())
                                               .foregroundColor(.green)
                                       } else {
                                           Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                                               .font(.title2.bold().monospacedDigit())
                                               .foregroundColor(.cyan)
                                               .frame(width: 80)
                                       }
                }
                DynamicIslandExpandedRegion(.center) {
                    Text(context.state.displayText) // Will show categoryName or "Finished"
                                           .font(.headline)
                                           .lineLimit(1)
                                           .foregroundColor(context.state.isFinished ? .green : .primary)
                }
                DynamicIslandExpandedRegion(.bottom) {
                    if !context.state.isFinished {
                        ProgressView(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: false)
                            .progressViewStyle(LinearProgressViewStyle())
                            .tint(.cyan)
                            .frame(height: 8, alignment: .top)
                            .clipped()
                    }
                }
            } compactLeading: {
                Image(systemName: context.state.isFinished ? "checkmark.circle.fill" : "timer")
                                   .foregroundColor(context.state.isFinished ? .green : .cyan)
                                   .padding([.leading], 2)
            } compactTrailing: {
                if context.state.isFinished {
                                  Text("Done")
                                      .font(.caption)
                                      .foregroundColor(.green)
                              } else {
                                  Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                                      .font(.caption.monospacedDigit())
                                      .foregroundColor(.cyan)
                                      .frame(width: 45)
                                      .padding([.trailing], 2)
                              }
            } minimal: {
                Image(systemName: context.state.isFinished ? "checkmark.circle.fill" : "timer.square")
                                    .foregroundColor(context.state.isFinished ? .green : .cyan)
            }
            .widgetURL(URL(string: "kittidoro://openTimer"))
            .keylineTint(Color.cyan)
        }
    }
}
