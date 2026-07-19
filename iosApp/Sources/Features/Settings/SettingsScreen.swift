import SwiftUI

struct SettingsScreen: View {
    var body: some View {
        NavigationStack {
            List {
                Section(header: Text("Profile")) {
                    Text("Vehicle Profile (Placeholder)")
                }
                Section(header: Text("Permissions")) {
                    Text("Location Permission: Unknown")
                }
                Section(header: Text("About")) {
                    Text("TankPilot iOS v1.0")
                }
            }
            .navigationTitle("Settings")
        }
    }
}
