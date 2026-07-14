TankPilot Phase 3A – Android Auto Foundation

Role

You are a senior Android automotive engineer specializing in Android Auto, the Android for Cars App Library, Kotlin Multiplatform, driver-distraction-safe UX, and fuel/vehicle telemetry applications.

This document is a planning artifact produced from a repository audit. No Android Auto code, manifest changes, or dependency changes have been made yet.

⸻

1. Recommended Android Auto Category and Justification

**Recommendation: `androidx.car.app.category.POI`**

Justification:

- Official Android for Cars documentation explicitly lists "parking spots, charging stations, and gas stations" as the example use case for the POI category.
- The two older, more specific categories — `androidx.car.app.category.PARKING` and `androidx.car.app.category.CHARGING` — were deprecated as of Car App Library v1.3 in favor of the unified `POI` category. TankPilot should target `POI` directly rather than a deprecated category.
- `PlaceListMapTemplate` — a host-rendered map with a scrollable list of places, which is the natural fit for the Fuel Rescue "Best Overall / Cheapest Reachable / Closest Safe" list — is gated to apps declaring `POI` (or the deprecated `PARKING`/`CHARGING`) categories. TankPilot needs this template, so `POI` is required, not just appropriate.
- **`androidx.car.app.category.NAVIGATION` is explicitly not recommended for this phase.** Per the product positioning constraint, TankPilot does not perform its own turn-by-turn guidance — it hands off to an external navigation app (Google Maps / Waze) once a station is chosen. The `NAVIGATION` category carries a much heavier requirement set (active-navigation state, continuous voice guidance, foreground navigation notification, `ActiveNavigationTemplate`/`NavigationTemplate`, dedicated audio-focus handling, and Play Store review as a navigation app). TankPilot does not meet these requirements today and should not claim the category. If TankPilot ever builds native in-app turn-by-turn (not currently planned), category would need to be revisited.
- No other category (`IOT`, `WEATHER`, `MEDIA`, `MESSAGING`, `CALLING`) applies to TankPilot's fuel/station domain.

A `CarAppService` may declare multiple categories, but there is no reason to add a second category for this phase — doing so would pull in quality-guideline obligations (e.g. `MEDIA`, `CALLING`) that don't apply to TankPilot and could complicate Play listing review.

Sources consulted: [Android for Cars App Library overview](https://developer.android.com/training/cars/apps/library), [Build a point of interest app](https://developer.android.com/training/cars/apps/poi), [Car app quality guidelines](https://developer.android.com/docs/quality-guidelines/car-app-quality).

⸻

2. Required Android for Cars Dependencies

Add to `gradle/libs.versions.toml` (new `[versions]` entry, e.g. `car-app = "1.7.0"` — confirm the current stable version against the [Jetpack release notes](https://developer.android.com/jetpack/androidx/releases/car-app) at implementation time, since this library ships frequent alpha/beta/stable cycles):

```
androidx.car.app:app:<version>              // core Car App Library (Screen, Template, CarAppService, Session)
androidx.car.app:app-projected:<version>     // Android Auto (phone-projected) host support
androidx.car.app:app-testing:<version>       // testImplementation only — Robolectric-style Screen/Template tests
```

`androidx.car.app:app-automotive` (embedded Android Automotive OS) is **not required for this phase**. TankPilot's product positioning is Android Auto (phone-projected). AAOS is a materially larger scope (own manifest category rules, `Car Ready`/`Parked Experiences` tier requirements, no phone present) and should be a separate future phase if pursued.

Library's own `minSdkVersion` requirement is **API 23**. TankPilot's `androidApp` `minSdk` is already 26 (`androidApp/build.gradle.kts`), so there is no conflict — no minSdk bump needed.

⸻

3. Minimum Supported Car App API Level

Recommend **`minCarApiLevel = 1`** for the initial release.

Rationale: the MVP screen set (Fuel Status via `PaneTemplate`, Fuel Rescue via `PlaceListMapTemplate`, Station Detail via `PaneTemplate`, Critical Fuel via `MessageTemplate`) uses long-established, broadly-supported templates. Starting at level 1 maximizes host compatibility (older Android Auto head units / phone host versions). If a later screen requires a feature gated to a higher `CarAppApiLevel` (check `androidx.car.app.CarAppApiLevels` and the specific template's `@RequiresCarApi` annotation at implementation time), raise the declared level then — don't pre-emptively raise it.

⸻

4. Required Manifest Entries

All additive to `androidApp/src/main/AndroidManifest.xml` — no existing entries (permissions, `MainActivity`, `TankPilotApplication`) need to change.

```xml
<uses-permission android:name="androidx.car.app.MAP_TEMPLATES"/>

<application>
    ...
    <service
        android:name=".car.TankPilotCarAppService"
        android:exported="true"
        android:label="@string/app_name"
        android:icon="@drawable/ic_launcher">
        <intent-filter>
            <action android:name="androidx.car.app.CarAppService"/>
            <category android:name="androidx.car.app.category.POI"/>
        </intent-filter>
    </service>

    <meta-data
        android:name="androidx.car.app.minCarApiLevel"
        android:value="1"/>
</application>
```

Notes:
- Current manifest uses `android:icon="@android:drawable/sym_def_app_icon"` (a placeholder system icon) at the `<application>` level — the car service's own icon/label should be revisited alongside real app branding, not blocked on this phase.
- No changes to `ACCESS_FINE_LOCATION`/`ACCESS_COARSE_LOCATION` (already declared) — Android Auto (phone-projected) runs in the same process/APK as the phone app, so existing location permissions cover it. No `foregroundServiceType` is needed since `CarAppService` is not a traditional foreground service.

⸻

5. Required Automotive Descriptor XML

Per current official documentation, there is **no separate `automotive_app_desc.xml` file** for the Car App Library's Android Auto (phone-projected) path — `minCarApiLevel` is declared inline as manifest `<meta-data>` (shown above). This differs from an older/legacy pattern some tutorials still reference; verify against the pinned library version's own sample/release notes at implementation time to avoid using a stale pattern.

An automotive `<meta-data>` XML descriptor (`android:name="com.android.systemui.action.AUTOMOTIVE_MODE"` era-style resource file) applies to embedded **Android Automotive OS**, which is out of scope per §2.

⸻

6. Proposed Source Structure

Keep Android Auto code inside the existing `:androidApp` module — the repo's established pattern is source-set-based variation (`src/debug`, `src/release`), not multi-module split, and Car App code needs the same Koin graph (`commonModule`, `appModule`) already wired in `TankPilotApplication`. A separate Gradle module is not justified for the current scope.

```
androidApp/src/main/java/com/tankpilot/android/car/
├── TankPilotCarAppService.kt        // CarAppService entry point
├── TankPilotCarSession.kt           // Session — returns initial Screen
├── di/
│   └── CarModule.kt                 // Koin module for car-scoped bindings, if any (likely thin)
├── screens/
│   ├── FuelStatusScreen.kt          // PaneTemplate
│   ├── FuelRescueScreen.kt          // PlaceListMapTemplate
│   ├── StationDetailScreen.kt       // PaneTemplate
│   └── CriticalFuelScreen.kt        // MessageTemplate
└── mapper/
    └── CarTemplateMappers.kt        // shared-domain model -> Car App Row/Place/Action mapping, no business logic
```

Naming collision to watch: `androidApp/src/main/java/com/tankpilot/android/ui/screens/FuelRescueScreen.kt` (Compose, phone) already exists. The car package above disambiguates by package (`car.screens` vs `ui.screens`), but pick distinct class names during implementation (e.g. `FuelRescueCarScreen`) to avoid import confusion.

New shared-module (`:shared`) additions (see §9 for why these are required, not optional):

```
shared/src/commonMain/kotlin/com/tankpilot/fuelrescue/domain/FuelRescueUseCase.kt
shared/src/commonMain/kotlin/com/tankpilot/location/domain/LocationProvider.kt
shared/src/commonMain/kotlin/com/tankpilot/fuelrescue/data/<RealRouteDistanceProvider>.kt   // or keep interface-only + wire a real impl per platform
```

⸻

7. Proposed Screens and Android Auto Templates

| Screen | Template | Why |
|---|---|---|
| Fuel Status | `PaneTemplate` | Small fixed set of glanceable key-value stats (percentage, gallons, safe range, confidence, fuel state) plus up to 2 primary actions ("Find Fuel"). Not map/list content, so `PaneTemplate` fits better than `PlaceListMapTemplate`. |
| Fuel Rescue | `PlaceListMapTemplate` | Category-gated to POI apps; purpose-built for "list of places + host-rendered map." Each of the 3 recommendations (Best Overall / Cheapest Reachable / Closest Safe) becomes a `Row`/`Place` item with distance, price, and freshness as text metadata. Supports `OnContentRefreshListener` for the refresh action. |
| Station Detail | `PaneTemplate` | Single-station glanceable detail (address, price, freshness, distance, arrival reserve) plus one primary action ("Navigate"). |
| Critical Fuel | `MessageTemplate` | Single blocking safety message: "No station is safely within the current estimate." No misleading action offered — see Architecture/Safety rules. |

**Navigate action mechanics (all screens that offer it):** do not attempt in-template turn-by-turn (that's the `NAVIGATION` category's job). Use an `Action` whose `OnClickListener` calls `CarContext.startCarApp(Intent)` with a `google.navigation:q=<lat>,<lng>` (or `geo:`) URI — the same handoff pattern the phone app's `FuelRescueScreen.kt:259` already uses via `Uri.parse(... "geo:${lat},${lng}...")`. This launches the user's chosen navigation app in Android Auto and is the sanctioned handoff mechanism for non-navigation-category apps.

⸻

8. Shared-Domain Use Cases That Can Be Reused

- **`FuelStateUseCase`** (`shared/src/commonMain/kotlin/com/tankpilot/fuel/domain/FuelStateUseCase.kt`) — already Koin-registered as a `single` in `commonModule`. Exposes `currentVehicle`, `estimatedFuelRemaining: StateFlow<Gallons>`, `confidence: StateFlow<ConfidenceLevel>`, `confidencePercent: StateFlow<Int>`, `safeRange: StateFlow<Miles>`. This is a direct, zero-duplication data source for the Fuel Status screen — inject it into the car `Session`/`Screen` exactly as `MainViewModel` does today.
- **`FuelRescueEngine.evaluateStations(...)`** (`shared/src/commonMain/kotlin/com/tankpilot/fuelrescue/domain/FuelRescueEngine.kt`) — pure, stateless scoring/ranking function. Reusable as-is once wrapped by an orchestration layer (see §9 — it currently has no such wrapper).
- **`FuelStationRepository`** (interface + `SqlDelightFuelStationRepository` impl) — already Koin-registered, already variant-aware (`NoOpFuelStationProvider` in release, `MockFuelStationProvider` in debug via `:testSupport`). Reusable as-is.
- **Core value types and enums** (`com.tankpilot.core.Units`, `com.tankpilot.core.Enums`) — `Gallons`, `Miles`, `MilesPerGallon`, `Money`, `FuelPricePerUnit`, `ConfidenceLevel`, `ReachabilityStatus`, `PriceFreshness` — reusable as-is for formatting car template text.

⸻

9. Missing Domain Interfaces That Must Be Added

These are **pre-existing gaps in `:shared`**, not new problems created by Android Auto — but Android Auto cannot ship a correct Fuel Rescue experience without them, because the current implementation lives entirely inside an Android `ViewModel` (`MainViewModel`) that a `CarAppService` cannot reuse.

1. **`FuelRescueUseCase`** (new, `com.tankpilot.fuelrescue.domain`). Today, `MainViewModel.refreshRescue()` (`androidApp/src/main/java/com/tankpilot/android/viewmodel/MainViewModel.kt:212-256`) is the *only* place that orchestrates "fetch stations → compute route distances → call `FuelRescueEngine.evaluateStations`." It is `ViewModel`-scoped and Android-lifecycle-bound — not callable from a `CarAppService`/`Session`. This orchestration needs to move into `:shared` so both the phone `MainViewModel` and the new car `Screen`s call the same use case. Without this, the only alternative is duplicating the orchestration logic in the car module, which violates the "do not duplicate station recommendation calculations" architecture rule.

2. **`RouteDistanceProvider` real implementation.** The interface already exists (`shared/src/commonMain/kotlin/com/tankpilot/fuelrescue/domain/RouteDistanceProvider.kt`) but **has no implementation and no Koin binding anywhere in the repo.** `MainViewModel.refreshRescue()` bypasses it entirely with an inline straight-line-distance heuristic (`routeDist = station.distanceMiles * 1.25 + 0.3`, assumed 35 mph average — `MainViewModel.kt:226-232`). This is a correctness/safety gap on the phone today, and it would carry directly into Android Auto's "arrival reserve" and "travel time" figures if left as-is. A real implementation (e.g. backed by a routing API) or, at minimum, a clearly-labeled improved estimate should be built before Fuel Rescue is presented as authoritative on Android Auto, since drivers may trust in-car UI more than a phone screen.

3. **`LocationProvider` (new).** There is **no real GPS location abstraction anywhere in `:shared`** — the `location` package only contains `HeadingProvider` (compass heading). On the phone, current location is not sourced from device GPS at all: `MainActivity.kt` calls `viewModel.refreshRescue(mockLatitude, mockLongitude, false)` with **hardcoded mock coordinates** (see call sites at `MainActivity.kt:80`, `:138`, `:161`). Android Auto (phone-projected) runs in the same process as the phone app, so a single shared `LocationProvider` interface (real impl via `FusedLocationProviderClient` on Android, mock impl in `:testSupport`) can serve both phone and car UI — but it must be built; today "current location" is fabricated everywhere it's used. **This is a blocker, not a nice-to-have** — Android Auto cannot ship real fuel-rescue station distances without it.

4. **A safety-eligibility → presentation mapping helper** (new, thin, in `:shared` or car mapper layer — see §"Architecture Rules" and Important Safety Rule below) that turns `ReachabilityStatus` into car-template-safe labels without ever upgrading status. This can live as a pure function next to the car mappers; it does not need to be a full "interface" but should be centrally defined once and reused across all car screens rather than re-implemented per screen.

⸻

10. Offline Behavior

- `FuelStateUseCase` reads from local repositories (`VehicleRepository`, `TripRepository`, `FillUpRepository`, all SQLDelight-backed) — Fuel Status screen works fully offline, same as the phone app.
- `FuelStationRepository`/`SqlDelightFuelStationRepository` already implements a stale-cache fallback (15-minute TTL, falls back to cached stations on provider error) — Fuel Rescue can show cached stations with visible `PriceFreshness` when offline, consistent with the "Cached-price behavior" requirement in §11.
- If no cached stations exist and the network/provider is unavailable, the Fuel Rescue screen should show an explicit "no station data available" state — distinct from, and less alarming than, the Critical Fuel "no safely reachable station" state (§ Architecture Rules — these must not be conflated).
- The release variant's `FuelStationProvider` is currently `NoOpFuelStationProvider` (always returns `emptyList()`) because **no real station-data provider (Google Places/Here/TomTom) has been implemented yet**, despite `StationProvider` enum already listing those options. This means Fuel Rescue — on phone or Android Auto — has **no live station data in a release build today**. This is a hard blocker for shipping Fuel Rescue on Android Auto (or phone) to real users; see §17 Risks.

⸻

11. Cached-Price Behavior

- `StationFuelPrice.freshness: PriceFreshness` (`RECENT`, `AGING`, `STALE`, `UNKNOWN`) is already modeled and already factored into `FuelRescueEngine`'s scoring (up to 10 pts) and reasoning strings.
- Android Auto glanceable copy must always surface freshness alongside any price (per the Proposed Screens spec: "Price freshness" is a required field on both Fuel Rescue and Station Detail). Never show a price without its freshness label — a stale price on a low-fuel screen is a safety-adjacent trust issue (driver could detour based on stale/wrong price).
- No change needed to the caching mechanism itself (`SqlDelightFuelStationRepository`'s grid-cell cache) — Android Auto consumes the same repository, same cache.

⸻

12. Navigation Handoff

- TankPilot does not implement in-app turn-by-turn. The "Navigate" action on Station Detail (and secondarily on Fuel Rescue list items) hands off via `CarContext.startCarApp(Intent)` using a `geo:`/`google.navigation:` URI, matching the phone app's existing `Uri.parse("geo:$lat,$lng?q=...")` pattern (`FuelRescueScreen.kt:259`).
- This keeps TankPilot correctly out of the `NAVIGATION` category (§1) — handoff, not guidance.
- `navigationDestination` is already a field on the shared `FuelStation` model — reuse it directly rather than re-deriving a URI in car code.

⸻

13. Release/Debug Provider Behavior

- Existing Koin `variantModule` pattern (`androidApp/src/debug/.../di/VariantModule.kt` vs `androidApp/src/release/.../di/VariantModule.kt`) already governs `FuelStationProvider` (mock vs. `NoOp`) and telemetry/heading/temperature providers. Android Auto should **consume the same `variantModule` bindings** — no new debug/release split is needed specifically for Car App code, since Car App screens depend on the same shared use cases and repositories as the phone app.
- **Do not** create a car-specific debug-only mock screen. Per the Architecture Rules, mock providers must remain debug-only at the *provider* level (already true), and no Test Lab or Developer OBD screen may be reachable from any `CarAppService`/`Screen` graph — since the car navigation stack is entirely new and separate from the phone's `Screen` enum/`when` state machine (§ audit finding below), this is naturally satisfied as long as car screens are never wired to those phone-only screens. No code from `ui/screens/testlab/` or `ui/screens/DeveloperObdScreen.kt` should be imported by anything under `car/`.
- Audit note (pre-existing, not Android-Auto-specific): `DeveloperObdScreen.kt`/`DeveloperObdViewModel.kt` live in `androidApp/src/main/...` (not a debug-only source set) and are compiled into every variant, including release — only UI-gated via a `BuildConfig.DEBUG` check in `HomeScreen.kt`. This doesn't block Android Auto (the car module simply never references it), but it's worth fixing independently since it's compiled into the release APK.

⸻

14. Desktop Head Unit Testing Approach

- Use the [Android Auto Desktop Head Unit (DHU)](https://developer.android.com/training/cars/testing/dhu), which requires: Android Auto companion app configured for developer mode on a connected test device (or emulator with a car-app-capable system image), `androidx.car.app:app-testing` for host-side interaction, and enabling "Unknown sources" for Android Auto if sideloading a debug build.
- Recommended validation matrix on DHU:
  - Fuel Status renders with real (or mock, in debug) `FuelStateUseCase` data, including the "unavailable" states (missing confidence, missing range) rendering as unavailable, not zero.
  - Fuel Rescue list renders 0/1/2/3 recommendations correctly, including the empty-state and the Critical Fuel message-only path.
  - `OnContentRefreshListener` on `PlaceListMapTemplate` triggers a real refresh (through `FuelRescueUseCase`, not a fake delay).
  - Navigate action correctly launches an external navigation app via `CarContext.startCarApp`.
  - Button response time and screen load time are visually confirmed to be well under the `DR-1` (2s) / `DR-3` (10s) quality thresholds — see §16.
  - Dark/light host theme switching (`MR-1`, though `MR-1` is scoped to apps that draw their own maps — TankPilot's `PlaceListMapTemplate` map is host-rendered, so this mainly applies to any custom iconography/coloring in Rows, not a self-drawn map).

⸻

15. Automated Test Approach

- `androidx.car.app:app-testing` (`testImplementation`) enables unit-testing `Screen`/`Template` construction without a real host — assert on `onGetTemplate()` output (correct template type, correct row/pane content, correct action wiring) for each screen given a fixed `FuelStateUseCase`/`FuelRescueUseCase` fake state.
- Reuse existing `:testSupport` fakes (`MockFuelStationProvider`, etc.) for car-screen tests exactly as the phone app's tests presumably do — do not build a parallel fake set.
- Add explicit tests asserting the Important Safety Rule (§ below): given a `ReachabilityStatus` of `MARGINALLY_REACHABLE`, `UNKNOWN`, or `OUTSIDE_SAFE_RANGE`, or a closed-at-arrival station, the car screen must never render a "recommended"/best-overall-style badge or copy for that station. These are pure-function tests against the mapper layer (§9.4) and don't require DHU.
- Add a regression test locking `FuelRescueEngine.evaluateStations` behavior unchanged by the new `FuelRescueUseCase` wrapper (i.e., the wrapper is orchestration-only, not a reimplementation).

⸻

16. Android Auto Quality Requirements

Applicable POI-category and general requirements (from the [Car app quality guidelines](https://developer.android.com/docs/quality-guidelines/car-app-quality)) that should be treated as acceptance criteria for this phase:

- **PF-1** — app must provide meaningful, driving-relevant functionality (satisfied: fuel/station discovery is inherently driving-relevant).
- **MR-1** — light/dark map theme support when the host requests it (applies to the host-rendered map in `PlaceListMapTemplate`; no custom map drawing needed for MVP).
- **DR-1 / DR-2 / DR-3** — app-specific button response ≤2s, app launch ≤10s, content load ≤10s. `FuelStateUseCase` is already reactive/local (fast); `FuelRescueUseCase` involves network — must show a loading state, not block template return.
- **ST-1 / SA-1** — no auto-scrolling text, no animated elements (canvas exception doesn't apply here — no custom map is drawn).
- **IU-1** — minimal image usage; only content/navigation icons and driving-decision imagery, no decorative images.
- **AN-1** — no dead ends; every screen must have a way back/forward (standard `Screen` back-stack behavior via `CarContext.getCarService(ScreenManager::class)`).
- **VD-1** — icon/color contrast requirements for car displays.
- **EP-2** — restore state as closely as possible on relaunch (straightforward given `FuelStateUseCase`'s reactive state).
- **DD-1** — since TankPilot is not `NAVIGATION` category, it must not attempt to use the navigation audio channel.

⸻

17. Risks or Blockers

1. **No real station data in release builds.** `FuelStationProvider` is `NoOpFuelStationProvider` in release — no Google Places/Here/TomTom integration exists. Fuel Rescue (phone or Auto) has nothing to show in a real release build today. **This blocks meaningful release of the Android Auto Fuel Rescue screen**, independent of any Car App Library work.
2. **No real GPS location source anywhere.** Phone code currently uses hardcoded `mockLatitude`/`mockLongitude` (`MainActivity.kt`). A real `LocationProvider` must be built (§9.3) before Android Auto (or the phone app) can show accurate distances/reachability.
3. **`RouteDistanceProvider` is unimplemented and unused** — real route distance/time (vs. the current straight-line-times-1.25 heuristic) is needed for `estimatedDriveMinutes` and `arrivalReserve` to be trustworthy on a safety-adjacent screen.
4. **No `FuelRescueUseCase` exists** — required to avoid duplicating rescue orchestration logic between phone `MainViewModel` and the new car module (architecture rule violation otherwise).
5. **Car App Library version currently unpinned** — must select and pin an exact `androidx.car.app` version at implementation start and verify manifest/meta-data patterns against that version's own docs, since the library is still shipping frequent releases.
6. **No README currently exists in the repository** (see below) — the requested "Android Auto Vision" section and concept-image reference cannot be added to an existing file because there isn't one yet. Flagged separately for your decision, not blocking this plan.
7. **`androidx-navigation-compose` is a declared but unused dependency** on the phone side (phone navigation is a hand-rolled `enum class Screen` + `when`, not `NavHost`) — not a blocker for Car App work (Car App has its own `Screen`/`ScreenManager` navigation model, unrelated to Compose Navigation), but noted so it isn't mistaken for existing infrastructure to build on.

⸻

18. Implementation Sequence

**Phase 3A-1 — Domain prerequisites (blocking, do first)**
- Add `LocationProvider` interface + real Android implementation + mock/test implementation.
- Add `FuelRescueUseCase` wrapping existing `FuelRescueEngine.evaluateStations` + `FuelStationRepository` + (initially, the existing straight-line heuristic, clearly marked as an interim `RouteDistanceProvider` stub — do not block this phase on solving real routing, but do not hide that it's an approximation).
- Migrate `MainViewModel.refreshRescue()` to call the new `FuelRescueUseCase` instead of inlining orchestration, proving the use case works for the existing phone screen before Android Auto depends on it.
- Wire both into `commonModule`/Koin.

**Phase 3A-2 — Car App Library scaffolding**
- Add `androidx.car.app` dependencies + version catalog entries.
- Add manifest `<service>`, permission, `minCarApiLevel` meta-data.
- Implement `TankPilotCarAppService` + `TankPilotCarSession` returning a placeholder `FuelStatusScreen`.
- Verify it launches in DHU before building out further screens.

**Phase 3A-3 — Screens**
- Fuel Status (`PaneTemplate`) using `FuelStateUseCase` directly.
- Critical Fuel (`MessageTemplate`) — build this alongside Fuel Rescue, not after, since it's a required branch of the same flow, not an afterthought.
- Fuel Rescue (`PlaceListMapTemplate`) using `FuelRescueUseCase`, including the safety-eligibility mapping (§9.4) gating what can appear as "recommended."
- Station Detail (`PaneTemplate`) + Navigate handoff.

**Phase 3A-4 — Verification**
- Unit tests per §15, including the Important Safety Rule regression tests.
- DHU pass per §14 validation matrix.
- Quality-guideline self-check per §16.

**Explicitly deferred to a later phase (not part of 3A):**
- Real station-data provider integration (Google Places or equivalent) — required for release-readiness but is its own scoped effort, not Car-App-specific.
- Real routing/ETA provider — same.
- Android Automotive OS (embedded) support.
- Voice/App Actions integration ("Hey Google, find gas stations on TankPilot").

⸻

Architecture Rules (carried forward from the task brief, restated as constraints on this plan)

- No fuel, confidence, or station-recommendation calculation may be reimplemented in car code — car screens only format output from `FuelEngine`, `ConfidenceEngine`, and `FuelRescueEngine` via the shared use cases in §8–9.
- Missing values (e.g. no confidence yet, no cached price) render as "unavailable" in car UI, never as `0`, `0%`, or a blank treated as zero.
- Mock providers stay debug-only (already true via `variantModule` — car code inherits this for free by depending on the same interfaces).
- No Test Lab or Developer OBD screen is reachable from the car `Screen`/`ScreenManager` graph.
- Android Auto must function with zero OBD adapter connected — `FuelStateUseCase` already doesn't require telemetry, only vehicle profile + trips + fill-ups, so this is satisfied by construction as long as car screens don't add a telemetry dependency.
- Phone app startup (`TankPilotApplication.onCreate` → `initKoin`) and phone navigation are unaffected — `CarAppService` is an additive Android component, not a replacement for `MainActivity`.

**Important Safety Rule:** Safety eligibility (`ReachabilityStatus`) must be resolved entirely inside `FuelRescueEngine`/`FuelRescueUseCase` before any car-template mapping happens. The mapper layer (§9.4) is presentation-only and must be structurally incapable of upgrading `MARGINALLY_REACHABLE`, `UNKNOWN`, or `OUTSIDE_SAFE_RANGE` into a "recommended" visual treatment, and must not present a closed-at-arrival station as open. Concretely: badge/label selection should be a total, exhaustive `when (reachabilityStatus)` (or sealed-class match) with no default/else branch, so a new enum value fails to compile rather than silently falling through to a "safe" default.
