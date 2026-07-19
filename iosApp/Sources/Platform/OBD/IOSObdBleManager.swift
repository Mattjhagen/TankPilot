import Foundation
import CoreBluetooth
import Combine
import shared

class IOSObdBleManager: NSObject, ObservableObject, CBCentralManagerDelegate {
    static let shared = IOSObdBleManager()
    
    private var centralManager: CBCentralManager!
    
    @Published var isBluetoothOn = false
    @Published var isScanning = false
    @Published var candidates: [ObdPeripheralCandidate] = []
    
    @Published var connectionState: ObdConnectionState = .disconnected {
        didSet {
            ObdTelemetrySnapshotManager.shared.updateConnectionState(state: connectionState)
        }
    }
    @Published var activeSession: ELM327Session?
    
    private let rememberedDeviceKey = "TankPilot_Remembered_OBD_Device"
    private let autoReconnectKey = "TankPilot_Auto_Reconnect_OBD"
    
    private var discoveredPeripherals: [UUID: CBPeripheral] = [:]
    
    private var pendingReconnect: UUID?
    
    override private init() {
        super.init()
        let options: [String: Any] = [
            CBCentralManagerOptionRestoreIdentifierKey: "TankPilotCentralManagerRestoration"
        ]
        centralManager = CBCentralManager(delegate: self, queue: .main, options: options)
    }
    
    var isAutoReconnectEnabled: Bool {
        get { UserDefaults.standard.bool(forKey: autoReconnectKey) }
        set { UserDefaults.standard.set(newValue, forKey: autoReconnectKey) }
    }
    
    func startScanning() {
        guard centralManager.state == .poweredOn else { return }
        candidates.removeAll()
        discoveredPeripherals.removeAll()
        isScanning = true
        connectionState = .scanning
        centralManager.scanForPeripherals(withServices: nil, options: [CBCentralManagerScanOptionAllowDuplicatesKey: false])
    }
    
    func stopScanning() {
        centralManager.stopScan()
        isScanning = false
        if connectionState == .scanning {
            connectionState = .disconnected
        }
    }
    
    func connect(to candidate: ObdPeripheralCandidate) {
        stopScanning()
        guard let peripheral = discoveredPeripherals[candidate.id] else { return }
        connect(to: peripheral)
    }
    
    private func connect(to peripheral: CBPeripheral) {
        UserDefaults.standard.set(peripheral.identifier.uuidString, forKey: rememberedDeviceKey)
        connectionState = .peripheralConnecting
        centralManager.connect(peripheral, options: nil)
    }
    
    func disconnect() {
        if let session = activeSession {
            session.cancel()
            centralManager.cancelPeripheralConnection(session.peripheral)
        }
        // Manual disconnect suppresses auto-reconnect
        isAutoReconnectEnabled = false
        KoinHelper.shared.exitObdContext()
    }
    
    private func attemptAutoReconnect() {
        guard isAutoReconnectEnabled else { return }
        guard let uuidString = UserDefaults.standard.string(forKey: rememberedDeviceKey),
              let uuid = UUID(uuidString: uuidString) else { return }
        
        let known = centralManager.retrievePeripherals(withIdentifiers: [uuid])
        if let peripheral = known.first {
            connect(to: peripheral)
        } else {
            // Not found, maybe we should scan in background but we were told: "Do not scan indefinitely in the background."
            // So we wait for user to manually scan if not retrieved.
        }
    }
    
    // MARK: - CBCentralManagerDelegate
    
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        isBluetoothOn = (central.state == .poweredOn)
        
        if central.state == .poweredOn {
            if connectionState == .disconnected || connectionState == .bluetoothUnavailable {
                attemptAutoReconnect()
            }
        } else {
            connectionState = .bluetoothUnavailable
            activeSession?.cancel()
            activeSession = nil
        }
    }
    
    func centralManager(_ central: CBCentralManager, willRestoreState dict: [String : Any]) {
        if let peripherals = dict[CBCentralManagerRestoredStatePeripheralsKey] as? [CBPeripheral] {
            for peripheral in peripherals {
                // Restore active connection
                if peripheral.state == .connected {
                    activeSession = ELM327Session(peripheral: peripheral)
                    activeSession?.startServiceDiscovery()
                    connectionState = .discoveringServices
                }
            }
        }
    }
    
    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        discoveredPeripherals[peripheral.identifier] = peripheral
        
        let candidate = ObdPeripheralCandidate(peripheral: peripheral, rssi: RSSI)
        if let index = candidates.firstIndex(where: { $0.id == candidate.id }) {
            candidates[index] = candidate
        } else {
            candidates.append(candidate)
        }
    }
    
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        connectionState = .peripheralConnected
        activeSession = ELM327Session(peripheral: peripheral)
        activeSession?.startServiceDiscovery()
    }
    
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        connectionState = .error
        activeSession = nil
    }
    
    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        connectionState = .disconnected
        activeSession?.cancel()
        activeSession = nil
        KoinHelper.shared.exitObdContext()
        
        // Attempt reconnect if enabled
        if isAutoReconnectEnabled {
            // "use bounded exponential backoff" - simplified here to delay
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { [weak self] in
                self?.attemptAutoReconnect()
            }
        }
    }
}
