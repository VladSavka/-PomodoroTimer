import ActivityKit
import WidgetKit
import SwiftUI

// TimerWidgetsAttributes struct remains the same
struct TimerWidgetsAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
      var categoryName: String
     }
     
     var startDate: Date
     var endDate: Date
}

struct TimerWidgetsLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: TimerWidgetsAttributes.self) { context in
            // --- LOCK SCREEN / BANNER UI ---
            VStack {
                HStack(spacing: 15) {
                    ZStack {
                        Image("pot")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 90, height: 90)
                        
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
                            
                                ProgressView(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: false) // countsDown should be false for progress
                                    .progressViewStyle(LinearProgressViewStyle())
                                    .tint(.white)
                                    .frame(height: 10)
    
                        }
                        .padding(.leading, 50)
                    }
                }
                .padding(.vertical, 15)
                .padding(.horizontal, 20)
            }
            .activityBackgroundTint(Color.black.opacity(0.7))
            .activitySystemActionForegroundColor(Color.white)

        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    Image(systemName: "timer")
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
                }
                DynamicIslandExpandedRegion(.bottom) {
                        ProgressView(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true)
                            .progressViewStyle(LinearProgressViewStyle())
                            .tint(.cyan)
                            .padding(.horizontal)

                }
            } compactLeading: {
                Image(systemName: "timer")
                    .foregroundColor(.cyan)
                    .padding(.leading, 2)
            } compactTrailing: {
                    Text(timerInterval: context.attributes.startDate...context.attributes.endDate, countsDown: true, showsHours: false)
                        .font(.caption.monospacedDigit())
                        .foregroundColor(.cyan)
                        .frame(width: 45)
    
            } minimal: {
                    Image(systemName: "timer.square")
                        .foregroundColor(.cyan)
            }
            .widgetURL(URL(string: "kittidoro://openTimer"))
            .keylineTint(Color.cyan)
        }
    }
}
