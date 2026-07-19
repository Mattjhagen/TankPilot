# Telemetry Architecture & Migration

## Existing Legacy Contract
- `VehicleTelemetryProvider`: Defines a flow of `TelemetryData`.
- `TelemetryData`: The large legacy telemetry state object.
- Consumers: `SpeedSelectionUseCase`, `DashboardActivationCoordinator`.

## New OBD Normalized Model
- `ObdTelemetrySnapshot`: The new purely SI-unit data class representing real-time telemetry updates.
- `ObdTelemetrySnapshotManager`: A Kotlin object to bridge iOS CoreBluetooth updates into the shared flow.

## Compatibility Mapping
- `ObdTelemetryCompatibilityAdapter` observes `ObdTelemetrySnapshotManager.snapshotFlow` and `connectionStateFlow`.
- Maps fields exactly when available (e.g., `speedKmh`, `engineRpm`, `coolantTempCelsius`).
- `ConnectionStatus` is mapped from the detailed `ObdConnectionState` to the legacy 4-state enum.

## Fields That Cannot Yet Be Represented
- Legacy fields like `engineRuntimeSeconds`, `checkEngineLightOn`, `diagnosticTroubleCodes` are currently missing in the new model.
- If these are required, they will be fetched in future OBD Mode 01 queries and added directly to `ObdTelemetrySnapshot`.

## Migration Plan
1. Validate `ObdTelemetryCompatibilityAdapter` in physical device tests (Phase B1.2).
2. Phase out `TelemetryData` in favor of injecting `ObdTelemetrySnapshot` (or a refined variant) directly into `SpeedArbitrator` and the Dashboard UI.
3. Update consumers (`SpeedSelectionUseCase`, `DashboardActivationCoordinator`) to rely on the new types.
4. Delete `ObdTelemetryCompatibilityAdapter`, `TelemetryData`, and legacy providers (`UnavailableTelemetryProvider`, `ObdTelemetryProvider`).

## Deletion Criteria
The adapter can be deleted when:
- `SpeedSelectionUseCase` is fully replaced by `SpeedArbitrator`.
- Dashboard UI subscribes to `ObdTelemetrySnapshot` instead of `TelemetryData`.
- Physical testing proves `ObdTelemetrySnapshot` successfully covers all real-time use cases.
