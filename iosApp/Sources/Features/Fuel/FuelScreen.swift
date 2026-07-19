import SwiftUI

struct FuelScreen: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("Fuel: 75% (Estimated)")
                Text("Range: 450 km (Estimated)")
                
                Divider()
                
                Text("Nearby Stations")
                    .font(.headline)
                
                Section(header: Text("Services")) {
                    Text("Fuel Rescue")
                        .font(.headline)
                }
            }
            .navigationTitle("Fuel")
        }
    }
}
