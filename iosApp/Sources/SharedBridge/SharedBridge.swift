import Foundation
import CoreLocation
import shared

@MainActor
class SharedBridge: ObservableObject {
    static let shared = SharedBridge()
    
    @Published var isInitialized: Bool = false
    @Published var errorMessage: String? = nil
    
    // Deterministic shared domain value
    @Published var domainTestValue: String = "Unknown"
    
    // Native location service
    let locationService = IOSLocationService()
    
    // Shared Session State
    @Published var currentSpeedMph: Double? = nil
    @Published var activeTripState: ActiveTripState = .idle
    @Published var elapsedTimeSeconds: Int64 = 0
    @Published var distanceMiles: Double = 0.0
    @Published var averageSpeedMph: Double? = nil
    @Published var maxSpeedMph: Double = 0.0
    @Published var routeCoordinates: [CLLocationCoordinate2D] = []
    
    // Shared Fuel State
    @Published var fuelPercentage: Double? = nil
    
    // OBD State
    @Published var engineRpm: Double? = nil
    @Published var coolantTempCelsius: Double? = nil
    @Published var isObdConnected: Bool = false
    @Published var selectedSpeedSource: String = "GPS"
    
    // Handles for the FlowWrappers
    private var sessionStateHandle: shared.Closeable? = nil
    private var fuelStateHandle: shared.Closeable? = nil
    private var routeHandle: shared.Closeable? = nil
    private var activeContextsHandle: shared.Closeable? = nil
    private var obdTelemetryHandle: shared.Closeable? = nil
    
    @Published var activeContexts: Set<VehicleContext> = []
    
    var activeContextDisplayName: String? {
        if activeContexts.contains(where: { String(describing: type(of: $0)).contains("CarPlay") }) {
            return "CarPlay"
        } else if activeContexts.contains(where: { String(describing: type(of: $0)).contains("AndroidAuto") }) {
            return "Android Auto"
        } else if activeContexts.contains(where: { String(describing: type(of: $0)).contains("Obd2") }) {
            return "OBD-II"
        } else if activeContexts.contains(where: { String(describing: type(of: $0)).contains("Bluetooth") }) {
            return "Bluetooth"
        } else if activeContexts.contains(where: { String(describing: type(of: $0)).contains("Manual") }) {
            return "Manual"
        }
        return nil
    }
    
    func initialize() {
        if isInitialized { return }
        
        do {
            KoinKt.doInitKoin()
            
            let coordinator = DashboardActivationCoordinator(isAutoModeEnabled: true, clock: SystemClock())
            self.domainTestValue = coordinator.dashboardMode.value != nil ? "Active" : "Unknown"
            
            // Connect to Koin singletons
            let drivingCoordinator = KoinHelper.shared.drivingSessionCoordinator
            locationService.setCoordinator(drivingCoordinator)
            
            // Subscribe to Flows
            self.sessionStateHandle = KoinHelper.shared.getDrivingSessionStateFlow().subscribe { [weak self] state in
                guard let self = self, let state = state as? DrivingSessionState else { return }
                DispatchQueue.main.async {
                    self.activeTripState = state.activeTripState
                    
                    if state.selectedSpeed.source != SpeedSource.unknown,
                       let kmh = state.selectedSpeed.valueKmh?.doubleValue {
                        self.currentSpeedMph = kmh * 0.621371
                    } else {
                        self.currentSpeedMph = nil
                    }
                    
                    if state.selectedSpeed.source == SpeedSource.obd {
                        self.selectedSpeedSource = "OBD"
                    } else {
                        self.selectedSpeedSource = "GPS"
                    }
                    
                    self.elapsedTimeSeconds = state.elapsedTimeSeconds
                    self.distanceMiles = state.distanceMeters * 0.000621371
                    
                    if let avgKmh = state.averageSpeedKmh?.doubleValue {
                        self.averageSpeedMph = avgKmh * 0.621371
                    } else {
                        self.averageSpeedMph = nil
                    }
                    
                    self.maxSpeedMph = state.maxSpeedKmh * 0.621371
                }
            }
            
            self.routeHandle = KoinHelper.shared.getRouteFlow().subscribe { [weak self] route in
                guard let self = self, let routeList = route as? [LocationSample] else { return }
                DispatchQueue.main.async {
                    self.routeCoordinates = routeList.map { sample in
                        CLLocationCoordinate2D(latitude: sample.latitude, longitude: sample.longitude)
                    }
                }
            }
            
            self.fuelStateHandle = KoinHelper.shared.getEstimatedFuelRemainingFlow().subscribe { [weak self] state in
                guard let self = self, let state = state as? KotlinDouble else { return }
                DispatchQueue.main.async {
                    self.fuelPercentage = state.doubleValue
                }
            }
            
            self.activeContextsHandle = KoinHelper.shared.getActiveContextsFlow().subscribe { [weak self] contexts in
                guard let self = self, let contextsSet = contexts as? Set<VehicleContext> else { return }
                DispatchQueue.main.async {
                    self.activeContexts = contextsSet
                }
            }
            
            self.obdTelemetryHandle = KoinHelper.shared.getObdTelemetryFlow().subscribe { [weak self] snapshot in
                guard let self = self, let snap = snapshot as? ObdTelemetrySnapshot else { return }
                DispatchQueue.main.async {
                    if let rpm = snap.rpm?.doubleValue {
                        self.engineRpm = rpm
                    } else {
                        self.engineRpm = nil
                    }
                    if let temp = snap.coolantTemperatureC?.doubleValue {
                        self.coolantTempCelsius = temp
                    } else {
                        self.coolantTempCelsius = nil
                    }
                }
            }
            
            // Just observe our iOS BLE Manager directly for connection status, no need for KMP flow
            NotificationCenter.default.addObserver(forName: NSNotification.Name("ObdConnectionStateChanged"), object: nil, queue: .main) { _ in
                // We'll just bind to IOSObdBleManager in the UI instead
            }
            
            self.isInitialized = true
        } catch {
            self.errorMessage = "Failed to initialize shared module: \(error.localizedDescription)"
        }
    }
    
    func startDrive() {
        if !isInitialized { return }
        
        locationService.requestPermission()
        if locationService.authorizationStatus == .notDetermined {
            // Will get callback in locationService when decided
            return
        }
        
        locationService.startUpdates()
        if locationService.errorMessage == nil {
            KoinHelper.shared.drivingSessionCoordinator.startTripManually()
        } else {
            self.errorMessage = locationService.errorMessage
        }
    }
    
    func stopDrive() {
        if !isInitialized { return }
        
        KoinHelper.shared.drivingSessionCoordinator.endTripManually()
        locationService.stopUpdates()
    }
}
