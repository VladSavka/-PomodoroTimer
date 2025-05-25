
import SwiftUI

struct PlainLinearProgressViewStyle: ProgressViewStyle {
    var barColor: Color
    var backgroundColor: Color
    var height: CGFloat

    // A helper view that only depends on the progress value for its width
    private struct ProgressBarFill: View {
        let progress: CGFloat
        let barColor: Color
        let height: CGFloat
        let availableWidth: CGFloat

        var body: some View {
            RoundedRectangle(cornerRadius: height / 2.0)
                .fill(barColor)
                .frame(width: availableWidth * progress) // Width based on progress
        }
    }

    func makeBody(configuration: Configuration) -> some View {
        GeometryReader { geometry in
            let currentProgress = CGFloat(configuration.fractionCompleted ?? 0.0)
            
            ZStack(alignment: .leading) {
                // Background
                RoundedRectangle(cornerRadius: height / 2.0)
                    .fill(backgroundColor)
                    .frame(width: geometry.size.width) // Full width for background
                
                // Foreground fill using the helper view
                ProgressBarFill(
                    progress: currentProgress,
                    barColor: barColor,
                    height: height,
                    availableWidth: geometry.size.width
                )
            }
        }
        .frame(height: height)
    }
}
