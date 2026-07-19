import Foundation
import CoreBluetooth
import Combine
import shared

enum ELMCommandState {
    case pending
    case executing
    case completed(response: String)
    case failed(reason: String)
}

struct ELMCommand {
    let id = UUID()
    let text: String
    let timeout: TimeInterval
    let retries: Int
}

class ELM327Session: NSObject, CBPeripheralDelegate, ObservableObject {
    let peripheral: CBPeripheral
    
    private var writeCharacteristic: CBCharacteristic?
    private var notifyCharacteristic: CBCharacteristic?
    
    @Published var discoveredCharacteristicsInfo: [String] = []
    
    @Published var initializationStep = "Not Started"
    @Published var isInitialized = false
    @Published var vehicleReady = false
    
    private var commandQueue: [ELMCommand] = []
    var queueDepth: Int { commandQueue.count }
    private var currentCommand: ELMCommand?
    private var currentBuffer = ""
    private var retryCount = 0
    private var commandStartTime: Date?
    private var timeoutWorkItem: DispatchWorkItem?
    
    @Published var lastCommand = ""
    @Published var lastResponse = ""
    @Published var averageLatencyMs = 0.0
    @Published var detectedProtocol = "Unknown"
    @Published var timeoutCount = 0
    private var latencies: [Double] = []
    
    // Polling scheduler
    private var pollingTimer: Timer?
    private var supportedPids: Set<ObdPid> = []
    
    init(peripheral: CBPeripheral) {
        self.peripheral = peripheral
        super.init()
        self.peripheral.delegate = self
    }
    
    func cancel() {
        pollingTimer?.invalidate()
        timeoutWorkItem?.cancel()
    }
    
    func startServiceDiscovery() {
        initializationStep = "Discovering Services"
        peripheral.discoverServices(nil)
    }
    
    // MARK: - CBPeripheralDelegate
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        guard let services = peripheral.services else { return }
        for service in services {
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        guard let characteristics = service.characteristics else { return }
        
        for char in characteristics {
            let props = char.properties
            var propsStrings = [String]()
            if props.contains(.write) { propsStrings.append("Write") }
            if props.contains(.writeWithoutResponse) { propsStrings.append("WriteWithoutResponse") }
            if props.contains(.notify) { propsStrings.append("Notify") }
            if props.contains(.indicate) { propsStrings.append("Indicate") }
            if props.contains(.read) { propsStrings.append("Read") }
            
            discoveredCharacteristicsInfo.append("\(char.uuid): \(propsStrings.joined(separator: ", "))")
            
            // Prefer writable command and notify/indicate response
            if (props.contains(.write) || props.contains(.writeWithoutResponse)) && writeCharacteristic == nil {
                writeCharacteristic = char
            }
            if (props.contains(.notify) || props.contains(.indicate)) && notifyCharacteristic == nil {
                notifyCharacteristic = char
            }
        }
        
        // If we found both, begin initialization
        if let notify = notifyCharacteristic, let write = writeCharacteristic {
            peripheral.setNotifyValue(true, for: notify)
            // Wait a bit for notify to establish before initializing
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
                self?.beginInitialization()
            }
        }
    }
    
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        guard characteristic == notifyCharacteristic, let data = characteristic.value, let string = String(data: data, encoding: .ascii) else { return }
        
        currentBuffer += string
        if currentBuffer.contains(">") {
            processCompleteResponse()
        }
    }
    
    // MARK: - Command Engine
    
    private func beginInitialization() {
        initializationStep = "Resetting (ATZ)"
        IOSObdBleManager.shared.connectionState = .adapterInitializing
        enqueueCommand("ATZ", timeout: 2.0, retries: 2)
        enqueueCommand("ATE0", timeout: 1.0, retries: 1)
        enqueueCommand("ATL0", timeout: 1.0, retries: 1)
        enqueueCommand("ATS0", timeout: 1.0, retries: 1)
        enqueueCommand("ATH0", timeout: 1.0, retries: 1)
        enqueueCommand("ATSP0", timeout: 2.0, retries: 1)
        enqueueCommand("ATDP", timeout: 1.0, retries: 1)
        enqueueCommand("ATI", timeout: 1.0, retries: 1)
    }
    
    func enqueueCommand(_ text: String, timeout: TimeInterval = 1.0, retries: Int = 0) {
        let cmd = ELMCommand(text: text, timeout: timeout, retries: retries)
        commandQueue.append(cmd)
        executeNextCommandIfReady()
    }
    
    private func executeNextCommandIfReady() {
        guard currentCommand == nil else { return } // Already executing
        guard !commandQueue.isEmpty else { return }
        
        let cmd = commandQueue.removeFirst()
        currentCommand = cmd
        currentBuffer = ""
        retryCount = 0
        
        sendCommandText(cmd.text, timeout: cmd.timeout)
    }
    
    private func sendCommandText(_ text: String, timeout: TimeInterval) {
        guard let writeChar = writeCharacteristic else { return }
        
        let writeType: CBCharacteristicWriteType = writeChar.properties.contains(.write) ? .withResponse : .withoutResponse
        let formatted = "\(text)\r"
        guard let data = formatted.data(using: .ascii) else { return }
        
        commandStartTime = Date()
        lastCommand = text
        peripheral.writeValue(data, for: writeChar, type: writeType)
        
        timeoutWorkItem?.cancel()
        let workItem = DispatchWorkItem { [weak self] in
            self?.handleTimeout()
        }
        timeoutWorkItem = workItem
        DispatchQueue.main.asyncAfter(deadline: .now() + timeout, execute: workItem)
    }
    
    private func handleTimeout() {
        guard let cmd = currentCommand else { return }
        if retryCount < cmd.retries {
            retryCount += 1
            currentBuffer = ""
            sendCommandText(cmd.text, timeout: cmd.timeout)
        } else {
            timeoutCount += 1
            finishCommand(success: false, response: "")
            
            if timeoutCount > 3 {
                IOSObdBleManager.shared.disconnect()
            }
        }
    }
    
    private func processCompleteResponse() {
        timeoutWorkItem?.cancel()
        
        if let startTime = commandStartTime {
            let latency = Date().timeIntervalSince(startTime) * 1000
            latencies.append(latency)
            if latencies.count > 10 { latencies.removeFirst() }
            averageLatencyMs = latencies.reduce(0, +) / Double(latencies.count)
        }
        
        let response = currentBuffer
        lastResponse = response
        timeoutCount = 0
        finishCommand(success: true, response: response)
    }
    
    private func finishCommand(success: Bool, response: String) {
        let completedCommand = currentCommand
        currentCommand = nil
        
        if let cmd = completedCommand {
            handleInitializationStep(command: cmd.text, response: response, success: success)
            if vehicleReady {
                handleTelemetryResponse(command: cmd.text, response: response)
            }
        }
        
        executeNextCommandIfReady()
    }
    
    private func handleInitializationStep(command: String, response: String, success: Bool) {
        if command == "ATI" && !isInitialized {
            isInitialized = true
            initializationStep = "Complete"
            discoverSupportedPids()
        }
        
        if command == "0100" {
            let normalized = ElmResponseNormalizer.shared.normalize(rawResponse: response, sentCommand: command)
            let result = SupportedPidDecoder.shared.decode(normalizedLines: normalized, basePid: "00")
            if let pids = result.first as? Set<ObdPid> {
                supportedPids.formUnion(pids)
            }
            if let hasNext = result.second as? Bool, hasNext {
                enqueueCommand("0120")
            } else {
                startVehicleSession()
            }
        } else if command == "0120" {
            startVehicleSession()
        }
    }
    
    private func handleTelemetryResponse(command: String, response: String) {
        let normalized = ElmResponseNormalizer.shared.normalize(rawResponse: response, sentCommand: command)
        
        if command == "010D", let speed = ObdPidDecoder.shared.decode(normalizedLines: normalized, pid: .vehicleSpeed) {
            ObdTelemetrySnapshotManager.shared.updateValue(pid: .vehicleSpeed, value: speed.doubleValue)
        } else if command == "010C", let rpm = ObdPidDecoder.shared.decode(normalizedLines: normalized, pid: .engineRpm) {
            ObdTelemetrySnapshotManager.shared.updateValue(pid: .engineRpm, value: rpm.doubleValue)
        }
    }
    
    private func discoverSupportedPids() {
        initializationStep = "Discovering PIDs"
        IOSObdBleManager.shared.connectionState = .searchingForProtocol
        enqueueCommand("0100", timeout: 2.0, retries: 2)
    }
    
    private func startVehicleSession() {
        guard !vehicleReady else { return }
        vehicleReady = true
        IOSObdBleManager.shared.connectionState = .vehicleConnected
        KoinHelper.shared.enterObdContext()
        startPolling()
    }
    
    private func startPolling() {
        // Very basic polling scheduler
        pollingTimer?.invalidate()
        pollingTimer = Timer.scheduledTimer(withTimeInterval: 0.33, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            guard self.commandQueue.isEmpty else { return } // don't overload
            
            if self.supportedPids.contains(ObdPid.vehicleSpeed) {
                self.enqueueCommand("010D")
            }
            if self.supportedPids.contains(ObdPid.engineRpm) {
                self.enqueueCommand("010C")
            }
        }
    }
}
