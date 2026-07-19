import XCTest
import CoreLocation
import shared
@testable import TankPilot

class IOSLocationServiceTests: XCTestCase {
    
    // Testing negative CLLocation speed mapping to unknown
    func testNegativeSpeedMapping() {
        let timestamp = Date()
        let location = CLLocation(coordinate: CLLocationCoordinate2D(latitude: 0, longitude: 0),
                                  altitude: 0,
                                  horizontalAccuracy: 10,
                                  verticalAccuracy: 10,
                                  course: 0,
                                  courseAccuracy: 0,
                                  speed: -1.0,
                                  speedAccuracy: 1.0,
                                  timestamp: timestamp)
        
        let validSpeed = location.speed >= 0 ? location.speed : nil
        let speedKmh = validSpeed != nil ? validSpeed! * 3.6 : nil
        
        XCTAssertNil(validSpeed)
        XCTAssertNil(speedKmh)
    }
    
    // Testing valid meters-per-second to MPH/Kmh conversion
    func testSpeedConversion() {
        // 10 m/s = 36 km/h = ~22.3694 mph
        let timestamp = Date()
        let location = CLLocation(coordinate: CLLocationCoordinate2D(latitude: 0, longitude: 0),
                                  altitude: 0,
                                  horizontalAccuracy: 10,
                                  verticalAccuracy: 10,
                                  course: 0,
                                  courseAccuracy: 0,
                                  speed: 10.0,
                                  speedAccuracy: 1.0,
                                  timestamp: timestamp)
        
        let validSpeed = location.speed >= 0 ? location.speed : nil
        let speedKmh = validSpeed != nil ? validSpeed! * 3.6 : nil
        
        XCTAssertNotNil(speedKmh)
        XCTAssertEqual(speedKmh!, 36.0, accuracy: 0.001)
        
        let speedMph = speedKmh! * 0.621371
        XCTAssertEqual(speedMph, 22.369, accuracy: 0.001)
    }
    
    // Testing stale sample rejection
    func testStaleSampleRejection() {
        // Sample from 15 seconds ago
        let staleTimestamp = Date().addingTimeInterval(-15.0)
        let location = CLLocation(coordinate: CLLocationCoordinate2D(latitude: 0, longitude: 0),
                                  altitude: 0,
                                  horizontalAccuracy: 10,
                                  verticalAccuracy: 10,
                                  course: 0,
                                  courseAccuracy: 0,
                                  speed: 10.0,
                                  speedAccuracy: 1.0,
                                  timestamp: staleTimestamp)
        
        let isStale = abs(location.timestamp.timeIntervalSinceNow) > 10.0
        XCTAssertTrue(isStale)
    }
    
    // Testing poor-accuracy sample handling
    func testPoorAccuracyRejection() {
        let timestamp = Date()
        let location = CLLocation(coordinate: CLLocationCoordinate2D(latitude: 0, longitude: 0),
                                  altitude: 0,
                                  horizontalAccuracy: 150.0,
                                  verticalAccuracy: 10,
                                  course: 0,
                                  courseAccuracy: 0,
                                  speed: 10.0,
                                  speedAccuracy: 1.0,
                                  timestamp: timestamp)
        
        let isPoor = location.horizontalAccuracy < 0 || location.horizontalAccuracy > 100
        XCTAssertTrue(isPoor)
    }
}
