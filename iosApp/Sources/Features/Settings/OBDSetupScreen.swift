import SwiftUI
import shared

struct OBDSetupScreen: View {
    @StateObject private var bleManager = IOSObdBleManager.shared
    
    var body: some View {
            List {
                Section(header: Text("Bluetooth Status")) {
                    HStack {
                        Text("State")
                        Spacer()
                        Text(bleManager.isBluetoothOn ? "On" : "Off")
                            .foregroundColor(bleManager.isBluetoothOn ? .green : .red)
                    }
                    
                    Toggle("Auto-Reconnect", isOn: Binding(
                        get: { bleManager.isAutoReconnectEnabled },
                        set: { bleManager.isAutoReconnectEnabled = $0 }
                    ))
                }
                
                Section(header: Text("Connection")) {
                    HStack {
                        Text("Adapter")
                        Spacer()
                        Text(connectionStateString(bleManager.connectionState))
                            .foregroundColor(.secondary)
                    }
                    
                    if bleManager.connectionState == .vehicleConnected {
                        HStack {
                            Text("Vehicle ECU")
                            Spacer()
                            Text("Ready")
                                .foregroundColor(.green)
                        }
                    } else if bleManager.connectionState == .peripheralConnected || bleManager.connectionState == .adapterInitializing {
                        HStack {
                            Text("Vehicle ECU")
                            Spacer()
                            Text("Waiting for vehicle")
                                .foregroundColor(.orange)
                        }
                    }
                    
                    if let session = bleManager.activeSession {
                        HStack {
                            Text("Initialization")
                            Spacer()
                            Text(session.initializationStep)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    if bleManager.connectionState != .disconnected && bleManager.connectionState != .bluetoothUnavailable {
                        Button("Disconnect") {
                            bleManager.disconnect()
                        }
                        .foregroundColor(.red)
                    }
                }
                
                if bleManager.connectionState == .disconnected {
                    Section(header: Text("Discovery")) {
                        if bleManager.isScanning {
                            HStack {
                                Text("Scanning...")
                                Spacer()
                                ProgressView()
                            }
                        } else {
                            Button("Scan for Adapters") {
                                bleManager.startScanning()
                            }
                        }
                        
                        ForEach(bleManager.candidates) { candidate in
                            Button(action: {
                                bleManager.connect(to: candidate)
                            }) {
                                HStack {
                                    VStack(alignment: .leading) {
                                        Text(candidate.name)
                                            .foregroundColor(.primary)
                                        Text(candidate.id.uuidString)
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }
                                    Spacer()
                                    Text("\(candidate.rssi) dBm")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("OBD-II Setup")
    }
    
    private func connectionStateString(_ state: ObdConnectionState) -> String {
        switch state {
        case .disconnected: return "Disconnected"
        case .bluetoothUnavailable: return "Bluetooth Off"
        case .permissionDenied: return "Permission Denied"
        case .scanning: return "Scanning..."
        case .peripheralConnecting: return "Connecting..."
        case .peripheralConnected: return "Connected"
        case .discoveringServices: return "Discovering Services..."
        case .resolvingCharacteristics: return "Resolving Characteristics..."
        case .adapterInitializing: return "Initializing Adapter..."
        case .searchingForProtocol: return "Searching Protocol..."
        case .vehicleConnected: return "Connected to ECU"
        case .adapterConnectedVehicleUnavailable: return "ECU Unavailable"
        case .disconnecting: return "Disconnecting..."
        case .error: return "Error"
        default: return "Unknown"
        }
    }
}
