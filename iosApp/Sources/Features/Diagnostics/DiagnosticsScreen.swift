import SwiftUI
import CoreLocation
import shared

struct DiagnosticsScreen: View {
    @EnvironmentObject var bridge: SharedBridge
    @Environment(\.presentationMode) var presentationMode
    @State private var timer: Timer? = nil
    @State private var now = Date()
    
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("GPS Status")) {
                    HStack {
                        Text("Status")
                        Spacer()
                        Text(isReceiving() ? "Receiving" : "Waiting...")
                            .foregroundColor(isReceiving() ? .green : .red)
                    }
                    HStack {
                        Text("Accuracy")
                        Spacer()
                        if let acc = bridge.locationService.horizontalAccuracy {
                            Text(String(format: "%.1f m", acc))
                                .foregroundColor(acc < 30 ? .green : .orange)
                        } else {
                            Text("--")
                        }
                    }
                    HStack {
                        Text("Samples")
                        Spacer()
                        Text("\(bridge.locationService.locationSampleCount)")
                    }
                    HStack {
                        Text("Last Sample")
                        Spacer()
                        if let last = bridge.locationService.lastUpdateTimestamp {
                            let delta = now.timeIntervalSince(last)
                            Text(String(format: "%.1f s ago", delta))
                                .foregroundColor(delta < 3.0 ? .primary : .red)
                        } else {
                            Text("--")
                        }
                    }
                }
                
                Section(header: Text("Trip Engine")) {
                    HStack {
                        Text("Drive State")
                        Spacer()
                        Text(bridge.activeTripState.name)
                            .foregroundColor(bridge.activeTripState == .active ? .green : .primary)
                    }
                    HStack {
                        Text("Vehicle Context")
                        Spacer()
                        Text(bridge.activeContextDisplayName ?? "Unknown")
                    }
                    HStack {
                        Text("Route Points")
                        Spacer()
                        Text("\(bridge.routeCoordinates.count)")
                    }
                    HStack {
                        Text("Speed Source")
                        Spacer()
                        Text("GPS")
                            .foregroundColor(.blue)
                    }
                }
                
                Section(header: Text("OBD Bluetooth")) {
                    HStack {
                        Text("Connection")
                        Spacer()
                        Text(IOSObdBleManager.shared.connectionState.name)
                            .foregroundColor(IOSObdBleManager.shared.connectionState == .vehicleConnected ? .green : .orange)
                    }
                    HStack {
                        Text("Session")
                        Spacer()
                        Text(IOSObdBleManager.shared.activeSession != nil ? "Active" : "None")
                    }
                    if let session = IOSObdBleManager.shared.activeSession {
                        HStack {
                            Text("Peripheral ID")
                            Spacer()
                            Text(session.peripheral.identifier.uuidString)
                                .font(.caption)
                                .lineLimit(1)
                                .truncationMode(.tail)
                        }
                        HStack {
                            Text("Init Step")
                            Spacer()
                            Text(session.initializationStep)
                        }
                        HStack {
                            Text("Cmd Queue")
                            Spacer()
                            Text("\(session.queueDepth)")
                        }
                        HStack {
                            Text("Characteristics")
                            Spacer()
                            Text("\(session.discoveredCharacteristicsInfo.count) found")
                        }
                        if !session.discoveredCharacteristicsInfo.isEmpty {
                            VStack(alignment: .leading) {
                                ForEach(session.discoveredCharacteristicsInfo, id: \.self) { info in
                                    Text(info)
                                        .font(.caption2)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                        HStack {
                            Text("Protocol")
                            Spacer()
                            Text(session.detectedProtocol ?? "Unknown")
                        }
                        HStack {
                            Text("Last Command")
                            Spacer()
                            Text(session.lastCommand ?? "--")
                                .font(.system(.caption, design: .monospaced))
                        }
                        HStack {
                            Text("Last Response")
                            Spacer()
                            Text(session.lastResponse ?? "--")
                                .font(.system(.caption, design: .monospaced))
                        }
                        HStack {
                            Text("Avg Latency")
                            Spacer()
                            Text(String(format: "%.0f ms", session.averageLatencyMs))
                        }
                        HStack {
                            Text("Timeouts")
                            Spacer()
                            Text("\(session.timeoutCount)")
                                .foregroundColor(session.timeoutCount > 0 ? .red : .primary)
                        }
                    }
                }
                
                Section(header: Text("System")) {
                    HStack {
                        Text("Background Updates")
                        Spacer()
                        Text(UIApplication.shared.backgroundRefreshStatus == .available ? "Enabled" : "Disabled")
                            .foregroundColor(UIApplication.shared.backgroundRefreshStatus == .available ? .green : .red)
                    }
                }
            }
            .navigationTitle("Developer Diagnostics")
            .navigationBarItems(trailing: Button("Done") {
                presentationMode.wrappedValue.dismiss()
            })
            .onAppear {
                timer = Timer.scheduledTimer(withTimeInterval: 0.5, repeats: true) { _ in
                    now = Date()
                }
            }
            .onDisappear {
                timer?.invalidate()
            }
        }
        .preferredColorScheme(.dark)
    }
    
    private func isReceiving() -> Bool {
        guard let last = bridge.locationService.lastUpdateTimestamp else { return false }
        return now.timeIntervalSince(last) < 5.0
    }
}
