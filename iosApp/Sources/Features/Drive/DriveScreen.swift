import SwiftUI
import shared

struct DriveScreen: View {
    @ObservedObject var bridge = SharedBridge.shared
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                if let error = bridge.errorMessage {
                    Text(error)
                        .foregroundColor(.red)
                        .padding()
                }
                
                Text("Status: \(tripStateString(bridge.activeTripState))")
                
                if let speedMph = bridge.currentSpeedMph {
                    Text("Speed: \(String(format: "%.1f", speedMph)) MPH")
                } else {
                    Text("Speed: Unknown (Waiting for GPS)")
                        .foregroundColor(.secondary)
                }
                
                Text("Elapsed: \(bridge.elapsedTimeSeconds)s")
                Text("Distance: \(String(format: "%.1f", bridge.distanceMiles)) miles")
                
                if bridge.activeTripState == .idle || bridge.activeTripState == .completing {
                    Button("Start Drive") {
                        bridge.startDrive()
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.blue)
                } else {
                    Button("Stop Drive") {
                        bridge.stopDrive()
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.red)
                }
            }
            .navigationTitle("Drive")
        }
    }
    
    func tripStateString(_ state: ActiveTripState) -> String {
        if state == ActiveTripState.idle { return "Idle" }
        if state == ActiveTripState.startCandidate { return "Starting" }
        if state == ActiveTripState.active { return "Active" }
        if state == ActiveTripState.stopCandidate { return "Stopping..." }
        if state == ActiveTripState.completing { return "Completing" }
        return "Unknown"
    }
}
