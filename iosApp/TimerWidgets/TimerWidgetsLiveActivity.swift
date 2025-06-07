import ActivityKit
import WidgetKit
import SwiftUI


public struct TimerWidgetsAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var categoryName: String
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
                        Text(context.state.categoryName)
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
                    Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                        .font(.title2.bold().monospacedDigit())
                        .foregroundColor(.cyan)
                        .frame(width: 80)
                }
                DynamicIslandExpandedRegion(.center) {
                    Text(context.state.categoryName)
                        .font(.headline)
                        .lineLimit(1)
                        .foregroundColor(.primary)
                }
                DynamicIslandExpandedRegion(.bottom) {
                    ProgressView(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: false)
                        .progressViewStyle(LinearProgressViewStyle())
                        .tint(.cyan)
                        .frame(height: 8, alignment: .top)
                        .clipped()
                    
                }
            } compactLeading: {
                
                Image(systemName: "timer")
                    .foregroundColor(.cyan)
                    .padding([.leading], 2)
                
            } compactTrailing: {
                Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                    .font(.caption.monospacedDigit())
                    .foregroundColor(.cyan)
                    .frame(width: 45)
                    .padding([.trailing], 2)
            } minimal: {
                Image(systemName: "timer.square").foregroundColor(.cyan)
            }
            .widgetURL(URL(string: "kittidoro://openTimer"))
            .keylineTint(Color.cyan)
        }
    }
}
