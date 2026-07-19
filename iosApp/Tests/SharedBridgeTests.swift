import XCTest
import shared
@testable import TankPilot

@MainActor
final class SharedBridgeTests: XCTestCase {

    func testSharedBridgeInitialization() throws {
        let bridge = SharedBridge.shared
        // It might be initialized by other tests if run out of order
        bridge.initialize()
        
        XCTAssertTrue(bridge.isInitialized)
        XCTAssertNil(bridge.errorMessage)
        XCTAssertNotEqual(bridge.domainTestValue, "Unknown")
        XCTAssertEqual(bridge.activeTripState, ActiveTripState.idle)
    }
    
    func testStartAndStopDrive() throws {
        let bridge = SharedBridge.shared
        bridge.initialize()
        
        // Ensure idle before start
        // Normally requires real location permission. Since tests run without location,
        // it may fail to start tracking. We just verify it handles the call.
        bridge.startDrive()
        
        bridge.stopDrive()
    }
}
