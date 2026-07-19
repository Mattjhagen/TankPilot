import SwiftUI
import MapKit
import shared

struct DriveScreen: View {
    @ObservedObject var bridge = SharedBridge.shared
    @State private var position: MapCameraPosition = .userLocation(followsHeading: true, fallback: .automatic)
    @State private var showingDiagnostics = false
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.black.ignoresSafeArea()
                
                VStack(spacing: 0) {
                    // Top: Status Row
                    statusRow
                        .padding(.top, 16)
                        .padding(.horizontal)
                    
                    Spacer()
                    
                    // Hero: Speedometer
                    heroSpeedometer
                        .padding(.vertical, 32)
                    
                    // Middle: Metrics Grid
                    metricsGrid
                        .padding(.horizontal)
                        .padding(.bottom, 24)
                    
                    // Bottom: MapKit
                    mapSection
                }
            }
            .navigationTitle("Drive")
            .navigationBarHidden(true)
            .sheet(isPresented: $showingDiagnostics) {
                DiagnosticsScreen()
            }
        }
    }
    
    private var statusRow: some View {
        HStack {
            HStack(spacing: 6) {
                Circle()
                    .fill(bridge.selectedSpeedSource == "GPS" ? (bridge.currentSpeedMph == nil ? Color.gray : Color.green) : Color.blue)
                    .frame(width: 8, height: 8)
                Text(bridge.selectedSpeedSource.uppercased())
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(bridge.selectedSpeedSource == "GPS" ? (bridge.currentSpeedMph == nil ? .gray : .green) : .blue)
            }
            
            if IOSObdBleManager.shared.connectionState == .vehicleConnected {
                Text("OBD")
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(.black)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(Color.blue)
                    .cornerRadius(4)
            }
            
            Spacer()
            
            Text(tripStateString(bridge.activeTripState).uppercased())
                .font(.caption)
                .fontWeight(.bold)
                .foregroundColor(.white)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(Color(white: 0.15))
                .cornerRadius(12)
            
            Spacer()
            
            // Start source
            Text(bridge.activeContextDisplayName?.uppercased() ?? "MANUAL")
                .font(.caption)
                .fontWeight(.bold)
                .foregroundColor(.gray)
        }
    }
    
    private var heroSpeedometer: some View {
        VStack(spacing: -8) {
            let speedValue = bridge.currentSpeedMph ?? 0.0
            
            Text(bridge.currentSpeedMph == nil ? "--" : String(format: "%.0f", speedValue))
                .font(.system(size: 110, weight: .bold, design: .rounded))
                .foregroundColor(.white)
                .shadow(color: bridge.currentSpeedMph != nil ? Color.blue.opacity(0.3) : .clear, radius: 20, x: 0, y: 0)
                .contentTransition(.numericText())
                .animation(.spring(response: 0.3, dampingFraction: 0.7), value: speedValue)
            
            Text("MPH")
                .font(.system(size: 24, weight: .semibold, design: .rounded))
                .foregroundColor(.gray)
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(bridge.currentSpeedMph == nil ? "Current speed unknown" : String(format: "Current speed %.0f miles per hour", bridge.currentSpeedMph!))
        .onLongPressGesture {
            showingDiagnostics = true
        }
    }
    
    private var metricsGrid: some View {
        VStack(spacing: 12) {
            if IOSObdBleManager.shared.connectionState == .vehicleConnected {
                HStack(spacing: 12) {
                    MetricCard(title: "RPM", value: bridge.engineRpm != nil ? String(format: "%.0f", bridge.engineRpm!) : "--", unit: "")
                    MetricCard(title: "COOLANT", value: bridge.coolantTempCelsius != nil ? String(format: "%.0f", bridge.coolantTempCelsius!) : "--", unit: "°C")
                }
            }
            HStack(spacing: 12) {
                MetricCard(title: "DISTANCE", value: String(format: "%.1f", bridge.distanceMiles), unit: "mi")
                MetricCard(title: "ELAPSED", value: formatElapsedTime(bridge.elapsedTimeSeconds), unit: "")
            }
            HStack(spacing: 12) {
                MetricCard(title: "AVG SPEED", value: bridge.averageSpeedMph != nil ? String(format: "%.1f", bridge.averageSpeedMph!) : "--", unit: "mph")
                MetricCard(title: "MAX SPEED", value: String(format: "%.1f", bridge.maxSpeedMph), unit: "mph")
            }
        }
    }
    
    private var mapSection: some View {
        ZStack(alignment: .bottomTrailing) {
            Map(position: $position) {
                if !bridge.routeCoordinates.isEmpty {
                    MapPolyline(coordinates: bridge.routeCoordinates)
                        .stroke(Color.blue, style: StrokeStyle(lineWidth: 5, lineCap: .round, lineJoin: .round))
                }
                UserAnnotation()
            }
            .mapControls {
                MapCompass()
                MapScaleView()
            }
            .cornerRadius(24, corners: [.topLeft, .topRight])
            .ignoresSafeArea(edges: .bottom)
            .onChange(of: bridge.activeTripState) { _, newState in
                if newState == .completing || (newState == .idle && !bridge.routeCoordinates.isEmpty) {
                    if !bridge.routeCoordinates.isEmpty {
                        withAnimation(.easeInOut(duration: 1.0)) {
                            position = .rect(boundingBox(for: bridge.routeCoordinates))
                        }
                    }
                } else if newState == .active {
                    withAnimation {
                        position = .userLocation(followsHeading: true, fallback: .automatic)
                    }
                }
            }
            
            VStack {
                Button(action: {
                    withAnimation {
                        position = .userLocation(followsHeading: true, fallback: .automatic)
                    }
                }) {
                    Image(systemName: "location.fill")
                        .font(.title3)
                        .foregroundColor(position == .userLocation(followsHeading: true, fallback: .automatic) ? .blue : .gray)
                        .padding(12)
                        .background(Color(white: 0.15))
                        .clipShape(Circle())
                }
                .padding()
                
                if bridge.activeTripState == .idle || bridge.activeTripState == .completing {
                    Button(action: { bridge.startDrive() }) {
                        Text("START DRIVE")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(16)
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 32)
                } else {
                    Button(action: { bridge.stopDrive() }) {
                        Text("STOP DRIVE")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.red)
                            .cornerRadius(16)
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 32)
                }
            }
        }
        .frame(height: 350)
    }
    private func boundingBox(for coordinates: [CLLocationCoordinate2D]) -> MKMapRect {
        guard !coordinates.isEmpty else { return MKMapRect.null }
        let polyline = MKPolyline(coordinates: coordinates, count: coordinates.count)
        let rect = polyline.boundingMapRect
        let widthPadding = rect.width * 0.2
        let heightPadding = rect.height * 0.2
        return rect.insetBy(dx: -widthPadding, dy: -heightPadding)
    }
    
    private func formatElapsedTime(_ seconds: Int64) -> String {
        let h = seconds / 3600
        let m = (seconds % 3600) / 60
        let s = seconds % 60
        if h > 0 {
            return String(format: "%d:%02d:%02d", h, m, s)
        } else {
            return String(format: "%02d:%02d", m, s)
        }
    }
    
    private func tripStateString(_ state: ActiveTripState) -> String {
        switch state {
        case .idle: return "Ready"
        case .startCandidate: return "Starting..."
        case .active: return "Driving"
        case .stopCandidate: return "Stopping..."
        case .completing: return "Completing..."
        default: return "Unknown"
        }
    }
}

struct MetricCard: View {
    let title: String
    let value: String
    let unit: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .fontWeight(.bold)
                .foregroundColor(.gray)
            
            HStack(alignment: .firstTextBaseline, spacing: 2) {
                Text(value)
                    .font(.system(size: 24, weight: .semibold, design: .rounded))
                    .foregroundColor(.white)
                
                if !unit.isEmpty {
                    Text(unit)
                        .font(.system(size: 14, weight: .medium, design: .rounded))
                        .foregroundColor(.gray)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color(white: 0.1))
        .cornerRadius(16)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("\(title): \(value) \(unit)")
    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}
