import SwiftUI

struct FuelScreen: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("Fuel: 75% (Estimated)")
                Text("Range: 450 km (Estimated)")
                
                Divider()
                
                Text("Nearby Stations (Placeholder)")
                    .font(.headline)
                
                Text("Fuel Rescue (Placeholder)")
                    .font(.headline)
            }
            .navigationTitle("Fuel")
        }
    }
}
