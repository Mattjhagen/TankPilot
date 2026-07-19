import SwiftUI
import shared

@main
struct TankPilotApp: App {
    @StateObject private var bridge = SharedBridge.shared
    
    init() {
    }
    
    var body: some Scene {
        WindowGroup {
            TabView {
                DashboardScreen()
                    .tabItem { Label("Dashboard", systemImage: "gauge") }
                DriveScreen()
                    .tabItem { Label("Drive", systemImage: "car") }
                FuelScreen()
                    .tabItem { Label("Fuel", systemImage: "fuelpump") }
                HistoryScreen()
                    .tabItem { Label("History", systemImage: "clock") }
                SettingsScreen()
                    .tabItem { Label("Settings", systemImage: "gear") }
            }
            .onAppear {
                bridge.initialize()
            }
        }
    }
}
