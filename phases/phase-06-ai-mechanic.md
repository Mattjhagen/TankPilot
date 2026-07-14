TankPilot Phase 6 – AI Mechanic & Diagnostic Intelligence

Role

You are a senior automotive software engineer, Kotlin Multiplatform architect, diagnostic systems engineer, and AI product designer.

Your task is to build TankPilot’s AI-assisted diagnostic and vehicle insight layer.

This phase must transform raw vehicle telemetry, diagnostic trouble codes, maintenance history, fuel economy trends, and user-entered symptoms into understandable, cautious, and actionable guidance.

TankPilot must not pretend to replace a certified mechanic.

It should help the user understand:

* What the vehicle is reporting
* How urgent the issue may be
* What symptoms are related
* Whether the issue may affect fuel economy
* What reasonable next steps exist
* When the vehicle may be unsafe to drive

The system must prioritize trust, explain uncertainty, and never fabricate mechanical conclusions.

⸻

Product Vision

TankPilot begins as a virtual fuel gauge.

With telemetry, maintenance history, and AI diagnostics, it becomes an intelligent vehicle companion.

The AI Mechanic should feel like a knowledgeable, honest mechanic sitting beside the user—not a generic chatbot and not an alarmist warning system.

The tone should be:

* Clear
* Calm
* Direct
* Non-technical by default
* Detailed when requested
* Honest about uncertainty

⸻

Phase Dependencies

Do not begin this phase until the following are stable:

* Phase 1 offline core
* Phase 1B Fuel Rescue
* Phase 2 trip detection
* Phase 4A OBD-II telemetry foundation
* Phase 4B live dashboard
* Phase 5 Vehicle Health and maintenance history

This phase may consume data from earlier phases but must not bypass their repository or domain boundaries.

⸻

Core Capabilities

1. Diagnostic Trouble Code Interpretation

Support interpretation of standard OBD-II trouble codes.

Examples:

* P0300
* P0420
* P0171
* P0442
* P0455
* P0128

For every code, display:

* Code
* Plain-language title
* Short explanation
* Possible causes
* Common symptoms
* Potential fuel economy impact
* Estimated urgency
* Whether continued driving may be risky
* Suggested next checks
* Date first observed
* Date last observed
* Whether the code is active, pending, permanent, or historical when available

Never present one possible cause as a confirmed diagnosis.

Use language such as:

* “Common causes include…”
* “This may be related to…”
* “The available data does not confirm the exact cause.”
* “A smoke test may be needed to confirm an EVAP leak.”
* “Further inspection is recommended.”

Do not say:

* “Your catalytic converter is definitely bad.”
* “Replace this part.”
* “This will cost exactly…”

unless the system has direct evidence, which standard OBD-II data generally does not provide.

⸻

2. Severity Classification

Create a deterministic severity system before AI-generated explanation.

enum class DiagnosticSeverity {
    INFORMATIONAL,
    LOW,
    MODERATE,
    HIGH,
    CRITICAL,
    UNKNOWN
}

Severity should be based on deterministic rules using:

* Trouble code category
* Check engine light state
* Flashing versus steady warning, when available
* Coolant temperature
* Oil pressure data, when supported
* Battery voltage
* Engine misfire data
* Engine load
* User-reported symptoms
* Whether the issue affects emissions only
* Whether the vehicle may stall or overheat
* Whether continued driving may cause damage

AI may explain the severity but must not be the sole authority assigning it.

Safety-critical classification must be rule-based and testable.

⸻

3. Drive Safety Guidance

Create:

enum class DriveSafetyStatus {
    NORMAL,
    MONITOR,
    SERVICE_SOON,
    LIMIT_DRIVING,
    STOP_WHEN_SAFE,
    DO_NOT_DRIVE,
    UNKNOWN
}

Examples:

NORMAL

No significant issue detected.

MONITOR

Minor issue or stale historical code.

SERVICE_SOON

Vehicle may continue operating, but inspection should be scheduled.

LIMIT_DRIVING

Avoid long trips, towing, heavy acceleration, or highway driving.

STOP_WHEN_SAFE

Pull over safely and shut off the engine when practical.

DO_NOT_DRIVE

Vehicle should not be driven until inspected.

The app must use conservative language.

When the evidence is incomplete, display uncertainty rather than assigning extreme severity.

⸻

AI Diagnostic Context

The AI Mechanic may use:

* Current diagnostic trouble codes
* Pending codes
* Historical codes
* Freeze-frame data
* Current RPM
* Engine load
* Coolant temperature
* Intake air temperature
* Battery voltage
* Fuel trims
* Mass airflow
* Manifold pressure
* Oxygen sensor data
* Vehicle speed
* Idle time
* Fuel economy history
* Learned city MPG
* Learned highway MPG
* Recent MPG changes
* Maintenance history
* Recent repairs
* User-reported symptoms
* Vehicle year, make, model, engine, and mileage
* Weather and ambient temperature, when available
* Recent fill-ups and fuel grade

Only include data that exists.

Never invent sensor readings or maintenance events.

⸻

User-Reported Symptoms

Add a guided symptom intake flow.

Symptoms may include:

* Rough idle
* Stalling
* Hard starting
* Loss of power
* Poor acceleration
* Shaking
* Overheating
* Fuel smell
* Burning smell
* Smoke
* Loud exhaust
* Clicking
* Grinding
* Squealing
* Battery warning
* Check engine light
* Flashing check engine light
* Reduced fuel economy
* Unusual vibration
* Transmission slipping
* Brake concerns
* Steering concerns

Allow free-text entry but also provide structured selections.

Record:

* When symptom started
* Whether symptom is constant or intermittent
* Whether it occurs cold, warm, idle, acceleration, braking, or highway driving
* Whether warning lights are present
* Whether the user recently had maintenance or repairs completed

Structured symptoms should be stored separately from AI-generated conclusions.

⸻

Diagnostic Explanation Screen

The main diagnostic result should include:

Header

* Vehicle
* Diagnostic severity
* Drive safety status
* Confidence level

Main explanation

Example:

“Your vehicle is reporting a random or multiple-cylinder misfire. Common causes include worn spark plugs, ignition coil problems, vacuum leaks, fuel-delivery issues, or low compression. Because the check engine light is flashing, continued driving may damage the catalytic converter.”

Why TankPilot thinks this

Display supporting evidence:

* P0300 detected
* Check engine light flashing
* Rough idle reported
* Fuel economy down 14%
* Positive fuel trim elevated
* Misfire detected under acceleration

Suggested next steps

Example:

1. Avoid hard acceleration.
2. Limit driving.
3. Check ignition and vacuum systems.
4. Schedule professional diagnosis.
5. Do not clear codes before inspection unless the user understands the consequences.

Estimated confidence

Examples:

* High confidence in severity
* Medium confidence in likely cause
* Low confidence in exact failed component

Separate confidence in severity from confidence in diagnosis.

⸻

Fuel Economy Correlation

The AI Mechanic should analyze whether maintenance or engine issues may be related to fuel consumption.

Examples:

* MPG decreased after a misfire code appeared.
* Long-term fuel trim suggests the engine may be running lean.
* Increased idle time explains part of the recent MPG decline.
* Low tire pressure may be contributing to reduced efficiency.
* A thermostat issue may prevent the engine from reaching efficient operating temperature.
* Fuel economy improved after spark plug replacement.

Do not imply causation from correlation alone.

Use phrases such as:

* “This may be contributing to…”
* “The timing suggests a possible relationship.”
* “The data shows correlation, not proof.”

⸻

Maintenance Recommendations

The AI Mechanic may suggest maintenance based on:

* Mileage
* Engine runtime
* Vehicle age
* Manufacturer schedule
* Past service history
* Diagnostic codes
* Fuel economy degradation
* User symptoms

Recommendations may include:

* Oil change
* Tire rotation
* Spark plugs
* Air filter
* Cabin air filter
* Coolant service
* Transmission service
* Brake inspection
* Battery test
* Fuel-system inspection
* EVAP system inspection
* Alignment
* Tire replacement

Never recommend replacing expensive parts solely from a generic trouble code.

Prefer inspection steps before replacement suggestions.

⸻

Repair Cost Guidance

Cost estimates must be broad and clearly labeled.

Represent:

data class RepairCostRange(
    val minimum: Money?,
    val maximum: Money?,
    val currencyCode: String,
    val confidence: CostEstimateConfidence,
    val sourceDescription: String
)
enum class CostEstimateConfidence {
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN
}

Costs may depend on:

* Vehicle
* Region
* Labor rates
* Whether diagnosis is included
* OEM versus aftermarket parts
* Independent shop versus dealership
* Whether additional damage exists

Display language like:

“Typical repair costs may vary widely. Inspection is needed before a reliable estimate can be provided.”

Do not give false precision.

⸻

Repair Shop Discovery

Add a future-ready provider abstraction:

interface RepairShopProvider {
    suspend fun findNearbyRepairShops(
        location: GeoPoint,
        specialty: RepairSpecialty?,
        radius: Distance
    ): List<RepairShop>
}

Potential specialties:

* General repair
* Electrical
* Transmission
* Tires
* Exhaust
* Emissions
* Brakes
* Mobile mechanic
* Dealership
* Towing
* Roadside assistance

Shop recommendations must eventually consider:

* Distance
* Hours
* User rating
* Review count
* Relevant specialty
* Whether open at arrival
* Diagnostic services offered
* Towing availability

Do not implement paid placement without clearly labeling sponsored results.

⸻

Diagnostic Session History

Create a vehicle-specific diagnostic timeline.

Each diagnostic session should store:

* Session ID
* Vehicle ID
* Timestamp
* Odometer
* Engine runtime
* Codes detected
* Live sensor snapshot
* Freeze-frame data
* User symptoms
* Severity
* Drive safety status
* AI explanation
* Suggested next steps
* Whether the issue was resolved
* Linked maintenance or repair event
* User notes

The user should be able to compare:

* Before repair
* After repair
* Code cleared
* Code returned
* Fuel economy before and after
* Symptoms before and after

⸻

Database Models

Add tables for:

DiagnosticSession

CREATE TABLE DiagnosticSession (
    id TEXT NOT NULL PRIMARY KEY,
    vehicleId TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    odometerMiles REAL,
    engineRuntimeSeconds INTEGER,
    severity TEXT NOT NULL,
    driveSafetyStatus TEXT NOT NULL,
    severityConfidence REAL NOT NULL,
    diagnosisConfidence REAL NOT NULL,
    aiSummary TEXT,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY(vehicleId)
        REFERENCES Vehicle(id)
        ON DELETE CASCADE
);

DiagnosticCodeEvent

CREATE TABLE DiagnosticCodeEvent (
    id TEXT NOT NULL PRIMARY KEY,
    diagnosticSessionId TEXT NOT NULL,
    code TEXT NOT NULL,
    status TEXT NOT NULL,
    title TEXT,
    firstObservedAt INTEGER,
    lastObservedAt INTEGER,
    FOREIGN KEY(diagnosticSessionId)
        REFERENCES DiagnosticSession(id)
        ON DELETE CASCADE
);

DiagnosticSensorSnapshot

CREATE TABLE DiagnosticSensorSnapshot (
    id TEXT NOT NULL PRIMARY KEY,
    diagnosticSessionId TEXT NOT NULL,
    pid TEXT NOT NULL,
    value REAL,
    unit TEXT,
    rawValue TEXT,
    capturedAt INTEGER NOT NULL,
    FOREIGN KEY(diagnosticSessionId)
        REFERENCES DiagnosticSession(id)
        ON DELETE CASCADE
);

ReportedSymptom

CREATE TABLE ReportedSymptom (
    id TEXT NOT NULL PRIMARY KEY,
    diagnosticSessionId TEXT NOT NULL,
    symptomType TEXT NOT NULL,
    severity TEXT,
    occurrencePattern TEXT,
    userDescription TEXT,
    FOREIGN KEY(diagnosticSessionId)
        REFERENCES DiagnosticSession(id)
        ON DELETE CASCADE
);

DiagnosticResolution

CREATE TABLE DiagnosticResolution (
    id TEXT NOT NULL PRIMARY KEY,
    diagnosticSessionId TEXT NOT NULL,
    maintenanceEventId TEXT,
    resolvedAt INTEGER,
    resolutionNotes TEXT,
    codeReturned INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(diagnosticSessionId)
        REFERENCES DiagnosticSession(id)
        ON DELETE CASCADE
);

Use migration files.

Do not delete diagnostic history when clearing active codes.

⸻

AI Architecture

Create a provider-neutral abstraction:

interface DiagnosticInsightProvider {
    suspend fun generateInsight(
        context: DiagnosticContext
    ): DiagnosticInsight
}

Support:

LocalRuleBasedDiagnosticProvider
RemoteAiDiagnosticProvider
MockDiagnosticInsightProvider

The deterministic rule engine must run first.

The AI layer should receive:

* Rule-engine severity
* Rule-engine safety status
* Vehicle data
* Codes
* Symptoms
* Telemetry
* Maintenance history
* Relevant trends

The AI layer may improve explanation and prioritization.

It must not override a stricter rule-based safety warning.

⸻

Offline Behavior

TankPilot must remain useful without internet access.

Offline mode should provide:

* Stored code definitions
* Rule-based severity
* Drive safety guidance
* Previously cached explanations
* Maintenance correlations
* Saved diagnostic history

Remote AI explanations may require connectivity.

If unavailable, show:

“Detailed AI explanation unavailable offline. Safety guidance and code interpretation remain available.”

Do not block diagnostic access because cloud AI is unavailable.

⸻

Privacy

Vehicle telemetry and diagnostic history are sensitive user data.

Requirements:

* Store diagnostic history locally by default
* Do not upload raw telemetry without user consent
* Explain when remote AI processing is used
* Allow users to disable cloud diagnostics
* Allow deletion of diagnostic sessions
* Avoid storing precise continuous location with diagnostic data unless necessary
* Redact VIN before remote AI transmission unless explicitly required
* Never use diagnostic data for advertising

⸻

Safety Guardrails

The AI Mechanic must never:

* Guarantee a diagnosis
* Guarantee repair cost
* Tell the user to ignore a flashing warning
* Encourage continued driving during overheating
* Recommend clearing codes as a substitute for repair
* Recommend bypassing emissions or safety systems
* Recommend disabling warning lights
* Recommend unsafe roadside repair
* Claim the app replaces a professional mechanic

For safety-critical conditions, display prominent guidance.

Examples:

Overheating

“Stop when safe and shut off the engine. Continued driving may cause severe engine damage.”

Flashing check engine light

“Limit driving and avoid hard acceleration. A flashing check engine light may indicate an active misfire that can damage the catalytic converter.”

Low oil pressure

“Stop the engine as soon as it is safe. Continued operation may cause severe engine damage.”

Brake or steering concern

“Do not continue driving if braking or steering feels unsafe.”

⸻

User Interface

The AI Mechanic UI should match TankPilot’s Tesla-inspired dashboard language.

Design characteristics:

* Dark background
* Large status labels
* Minimal clutter
* Digital twin as visual anchor
* Static severity accents
* No flashing animations
* Smooth state transitions
* Clear typography
* Plain-language summaries

Suggested screen layout:

[ Digital Twin Vehicle ]
SERVICE SOON
P0420
Catalyst System Efficiency Below Threshold
Drive Status
Monitor
Fuel Economy Impact
Possible
Confidence
Severity: High
Exact Cause: Low
Why TankPilot flagged this
• Code detected twice
• MPG down 8%
• No drivability symptoms reported
Recommended next step
Schedule an emissions-system inspection

⸻

AI Chat Experience

Add an optional vehicle-specific conversation screen.

The user may ask:

* “Can I keep driving?”
* “What does P0420 mean?”
* “Could this explain my bad gas mileage?”
* “What should I ask the mechanic?”
* “What should they inspect first?”
* “Did this start before or after my oil change?”
* “What changed since last month?”
* “Is this likely electrical or mechanical?”

The assistant must answer using the current vehicle context.

It must distinguish:

* Known facts
* Likely possibilities
* Unknowns
* Safety recommendations

The AI should reference supporting vehicle data in plain language.

⸻

Mechanic Visit Summary

Generate a clean summary the user can show a mechanic.

Include:

* Vehicle details
* Current mileage
* Codes
* Symptoms
* When the problem began
* Sensor observations
* MPG changes
* Relevant maintenance
* Whether the warning is intermittent
* Questions the user wants answered

Example:

2003 Chevrolet Impala
3.4L V6
147,422 miles
Issue:
Rough idle and reduced fuel economy
Codes:
P0300 – Random/Multiple Cylinder Misfire
Observed:
• Rough idle when warm
• MPG declined from 21.3 to 18.7
• Positive fuel trim elevated
• Code returned after clearing
Recent Maintenance:
• Spark plugs replaced 8,000 miles ago
• Air filter replaced 2,000 miles ago

Support export as:

* Share sheet text
* PDF in a later document-export phase
* Copy to clipboard

Do not include unsupported conclusions.

⸻

Verification Requirements

Add tests covering:

1. Rule-based safety status cannot be weakened by AI.
2. Unsupported sensor values are excluded from context.
3. Missing telemetry does not cause fabricated findings.
4. A flashing check engine light raises severity.
5. Overheating produces STOP_WHEN_SAFE or stricter.
6. Low oil pressure produces DO_NOT_DRIVE or equivalent strict guidance.
7. Historical codes are not treated as active.
8. Pending codes are labeled correctly.
9. AI explanations distinguish possible cause from confirmed cause.
10. Diagnostic sessions remain after codes are cleared.
11. Linked repairs can mark a diagnostic session resolved.
12. Returned codes reopen or update the issue.
13. Fuel economy correlations are labeled as correlations.
14. Offline code interpretation works without network access.
15. Remote AI can be disabled.
16. VIN is redacted before remote transmission.
17. Cost estimates are always ranges.
18. Mechanic summaries contain only recorded facts.
19. Diagnostic history deletion does not delete unrelated maintenance history.
20. Safety-critical UI never uses distracting flashing animation.

⸻

Implementation Sequence

Phase 6A – Diagnostic Domain

* Diagnostic models
* Severity engine
* Drive safety rules
* Code database
* Diagnostic repositories
* Migrations
* Tests

Phase 6B – Diagnostic Capture

* OBD-II code capture
* Freeze-frame support
* Sensor snapshots
* Symptom intake
* Diagnostic timeline

Phase 6C – AI Explanation

* Provider-neutral AI interface
* Context builder
* Guardrails
* Local rule-based fallback
* Remote AI implementation
* Privacy controls

Phase 6D – Mechanic Experience

* AI Mechanic screen
* Vehicle-specific chat
* Mechanic visit summary
* Repair-shop provider abstraction
* Resolution tracking

Do not skip directly to AI generation before deterministic severity rules and diagnostic persistence are complete.

⸻

Completion Criteria

Phase 6 is complete when:

* Diagnostic sessions can be captured and stored
* Standard OBD-II codes can be interpreted offline
* Severity and drive-safety guidance are deterministic
* AI explanations are grounded in stored data
* The AI cannot weaken safety warnings
* Users can record symptoms
* Users can link repairs to diagnostic sessions
* Fuel-economy changes can be correlated with issues
* A mechanic-ready summary can be generated
* All privacy and safety tests pass

At the end of implementation, produce:

* Architecture summary
* Database migration summary
* Supported diagnostic inputs
* Offline capability summary
* AI privacy summary
* Test results
* Known vehicle and adapter limitations
