import Foundation
import CoreBluetooth

struct ObdPeripheralCandidate: Identifiable, Equatable {
    let id: UUID
    let name: String
    let rssi: Int
    
    init(peripheral: CBPeripheral, rssi: NSNumber) {
        self.id = peripheral.identifier
        self.name = peripheral.name ?? "Unknown Device"
        self.rssi = rssi.intValue
    }
}
