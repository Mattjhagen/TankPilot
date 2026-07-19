import Foundation
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
    
    // Shared Fuel State
    @Published var fuelPercentage: Double? = nil
    
    // Handles for the FlowWrappers
    private var sessionStateHandle: shared.Closeable? = nil
    private var fuelStateHandle: shared.Closeable? = nil
    
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
                    
                    self.elapsedTimeSeconds = state.elapsedTimeSeconds
                    self.distanceMiles = state.distanceMiles
                }
            }
            
            self.fuelStateHandle = KoinHelper.shared.getEstimatedFuelRemainingFlow().subscribe { [weak self] state in
                guard let self = self, let state = state as? KotlinDouble else { return }
                DispatchQueue.main.async {
                    self.fuelPercentage = state.doubleValue
                }
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
