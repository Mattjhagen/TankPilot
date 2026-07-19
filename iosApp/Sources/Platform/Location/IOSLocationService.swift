import Foundation
import CoreLocation
import shared // KMP module

class IOSLocationService: NSObject, CLLocationManagerDelegate, ObservableObject {
    private let locationManager = CLLocationManager()
    
    @Published var authorizationStatus: CLAuthorizationStatus = .notDetermined
    @Published var isLocationEnabled: Bool = false
    
    // In meters per second
    @Published var currentSpeedMps: Double? = nil
    @Published var horizontalAccuracy: Double? = nil
    @Published var lastUpdateTimestamp: Date? = nil
    
    @Published var errorMessage: String? = nil

    // Reference to the shared coordinator to feed location samples
    private var coordinator: DrivingSessionCoordinator? = nil
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        locationManager.activityType = .automotiveNavigation
        locationManager.distanceFilter = kCLDistanceFilterNone
        locationManager.pausesLocationUpdatesAutomatically = true
        
        self.authorizationStatus = locationManager.authorizationStatus
        self.isLocationEnabled = CLLocationManager.locationServicesEnabled()
    }
    
    func setCoordinator(_ coordinator: DrivingSessionCoordinator) {
        self.coordinator = coordinator
    }
    
    func requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    func startUpdates() {
        if !CLLocationManager.locationServicesEnabled() {
            errorMessage = "Location services are disabled."
            return
        }
        if authorizationStatus == .denied || authorizationStatus == .restricted {
            errorMessage = "Location permission denied."
            return
        }
        errorMessage = nil
        locationManager.startUpdatingLocation()
    }
    
    func stopUpdates() {
        locationManager.stopUpdatingLocation()
        currentSpeedMps = nil
        horizontalAccuracy = nil
    }
    
    // MARK: - CLLocationManagerDelegate
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        self.authorizationStatus = manager.authorizationStatus
        self.isLocationEnabled = CLLocationManager.locationServicesEnabled()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        
        // Reject stale samples (older than 10 seconds)
        if abs(location.timestamp.timeIntervalSinceNow) > 10.0 {
            return
        }
        
        // Reject inaccurate samples (e.g., > 100 meters)
        if location.horizontalAccuracy < 0 || location.horizontalAccuracy > 100 {
            return
        }
        
        // Negative speed indicates invalid speed
        let speed = location.speed
        let validSpeed = speed >= 0 ? speed : nil
        let speedKmh = validSpeed != nil ? validSpeed! * 3.6 : nil
        let speedAccuracyMps = location.speedAccuracy >= 0 ? location.speedAccuracy : nil
        
        self.currentSpeedMps = validSpeed
        self.horizontalAccuracy = location.horizontalAccuracy
        self.lastUpdateTimestamp = location.timestamp
        
        // Push to shared domain
        let sample = LocationSample(
            timestamp: Kotlinx_datetimeInstant.Companion().fromEpochMilliseconds(epochMilliseconds: Int64(location.timestamp.timeIntervalSince1970 * 1000)),
            latitude: location.coordinate.latitude,
            longitude: location.coordinate.longitude,
            speedKmh: speedKmh != nil ? KotlinDouble(value: speedKmh!) : nil,
            speedAccuracyMps: speedAccuracyMps != nil ? KotlinDouble(value: speedAccuracyMps!) : nil,
            horizontalAccuracyMeters: KotlinDouble(value: location.horizontalAccuracy),
            bearingDegrees: location.course >= 0 ? KotlinDouble(value: location.course) : nil,
            roadContext: RoadContext.unknown,
            source: LocationSampleSource.gps
        )
        
        let currentWallClockTime = Kotlinx_datetimeInstant.Companion().fromEpochMilliseconds(epochMilliseconds: Int64(Date().timeIntervalSince1970 * 1000))
        coordinator?.onRawLocationUpdate(sample: sample, currentWallClockTime: currentWallClockTime)
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        if let clError = error as? CLError {
            switch clError.code {
            case .denied:
                errorMessage = "Location access denied."
                stopUpdates()
            case .locationUnknown:
                errorMessage = "Waiting for GPS..."
            default:
                errorMessage = "Location error: \(clError.localizedDescription)"
            }
        }
    }
}
