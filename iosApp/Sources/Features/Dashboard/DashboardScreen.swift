import SwiftUI
import shared

struct DashboardScreen: View {
    @ObservedObject var bridge = SharedBridge.shared
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("TankPilot")
                    .font(.largeTitle)
                    .bold()
                
                if bridge.isInitialized {
                    Text("Connection: Active")
                        .foregroundColor(.green)
                    Text("Test Value: \(bridge.domainTestValue)")
                        .font(.caption)
                        .foregroundColor(.gray)
                } else if let error = bridge.errorMessage {
                    Text("Connection: Failed (\(error))")
                        .foregroundColor(.red)
                } else {
                    Text("Connection: Initializing...")
                        .foregroundColor(.orange)
                }
                
                Text("Location Services: \(locationStatusText())")
                    .foregroundColor(bridge.locationService.isLocationEnabled ? .green : .red)
                
                Text("Current Trip: \(tripStateString(bridge.activeTripState))")
                
                if let speedMph = bridge.currentSpeedMph {
                    Text("Speed: \(String(format: "%.1f", speedMph)) MPH")
                } else {
                    Text("Speed: Unavailable")
                        .foregroundColor(.secondary)
                }
                
                if let fuel = bridge.fuelPercentage {
                    Text("Fuel: \(String(format: "%.1f%%", fuel * 100))")
                } else {
                    Text("Fuel: Unavailable")
                        .foregroundColor(.secondary)
                }
                
                Text("Range: Unavailable (Placeholder Estimate)")
                    .foregroundColor(.secondary)
                
                if bridge.activeTripState == .idle || bridge.activeTripState == .completing {
                    Button("Quick Start Drive") {
                        bridge.startDrive()
                    }
                    .buttonStyle(.borderedProminent)
                } else {
                    Button("Stop Drive") {
                        bridge.stopDrive()
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.red)
                }
            }
            .navigationTitle("Dashboard")
        }
    }
    
    func locationStatusText() -> String {
        switch bridge.locationService.authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways: return "Authorized"
        case .denied, .restricted: return "Denied"
        case .notDetermined: return "Not Determined"
        @unknown default: return "Unknown"
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
