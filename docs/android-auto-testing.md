# TankPilot — Android Auto Desktop Head Unit (DHU) Testing

This is a testing procedure, not a compliance sign-off. Passing every scenario here
confirms TankPilot behaves correctly against the Car App Library host simulator (DHU).
**It does not constitute validation in a real vehicle head unit** — screen sizes, input
methods, and OEM host behavior vary, and DHU cannot fully substitute for in-car testing
before a release is trusted on real hardware.

Covers Phase 3A.1–3A.3 (root screen, Fuel Rescue, Critical state) from
[phases/phase-03a-android-auto-foundation.md](../phases/phase-03a-android-auto-foundation.md).

---

## 1. Required tools

| Tool | Where it comes from | Notes |
|---|---|---|
| Android Studio | [developer.android.com/studio](https://developer.android.com/studio) | Needed for the SDK Manager, even if you build TankPilot from the CLI. |
| **SDK Tools → Android Auto Desktop Head Unit Emulator** | Android Studio → **Settings/Preferences → Languages & Frameworks → Android SDK → SDK Tools** tab (check "Show Package Details" if it's hidden) | Installs to `$ANDROID_HOME/extras/google/auto/desktop-head-unit`. There is no CLI (`sdkmanager`) fallback documented for this package as of this writing — install it through Android Studio's SDK Manager UI. |
| `adb` | Android SDK platform-tools (`$ANDROID_HOME/platform-tools`) | Already required for normal TankPilot development. |
| A physical Android phone | Android 9 (API 28) or newer | The DHU does not run against the Android emulator for Auto testing — Google's own docs specify a real device. |
| Android Auto app | [Play Store](https://play.google.com/store/apps/details?id=com.google.android.projection.gearhead) | Must be installed and up to date on the phone. |

You do **not** need `androidx.car.app:app-automotive` or an Android Automotive OS system
image for this — TankPilot targets Android Auto (phone-projected) only, per
[phase-03a-android-auto-foundation.md §2](../phases/phase-03a-android-auto-foundation.md).

---

## 2. Enable Android Auto developer mode (on the phone)

1. Open **Settings → Apps → Android Auto** (or open the Android Auto app directly).
2. Scroll to the **Version** entry and tap it **10 times** until a confirmation dialog
   appears offering to enable developer settings.
3. Open the Android Auto app's overflow menu (⋮) → **Developer settings**.

## 3. Enable Unknown sources (required for a debug/sideloaded build)

TankPilot's debug build is not distributed through Google Play, so Android Auto will
not show it by default.

1. In **Developer settings** (from step 2), scroll to **Unknown sources** and enable it.
2. Back out to the main Android Auto **Settings**, open **Previously connected cars**,
   and make sure **Add new cars to Android Auto** is enabled.
3. If TankPilot still doesn't appear after installing it, check the launcher
   customization screen in Android Auto settings — sideloaded apps sometimes need to be
   manually enabled there.

## 4. Build and install TankPilot (debug)

From the repo root:

```bash
./gradlew :androidApp:assembleDebug
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

Confirm the Car App Service is registered:

```bash
adb shell dumpsys package com.tankpilot.android | grep -A3 "androidx.car.app.CarAppService"
```

You should see `com.tankpilot.android.auto.TankPilotCarAppService` with category
`androidx.car.app.category.POI`.

## 5. Start the head unit server (on the phone)

1. Open Android Auto's overflow menu (⋮) → **Start head unit server**.
2. A persistent notification confirms the server is running. Leave the phone **unlocked**.

## 6. Connect the phone and forward the port

Plug the phone into the development machine over USB, then:

```bash
adb devices                 # confirm the phone shows as "device", not "unauthorized"
adb forward tcp:5277 tcp:5277
adb forward --list          # confirm the forward is active
```

## 7. Launch DHU on macOS

```bash
cd "$ANDROID_HOME/extras/google/auto"
chmod +x ./desktop-head-unit   # first run only
./desktop-head-unit
```

On the phone, accept the terms-of-service / permissions prompt that appears when DHU
first connects. If the DHU window stays blank, see [Troubleshooting](#troubleshooting)
below before assuming TankPilot is broken.

**Alternative (USB/accessory mode, DHU 2.x):** `./desktop-head-unit --usb` — skips
`adb forward` but requires the phone to support AOA accessory mode.

## 8. Launch TankPilot in the DHU

TankPilot should appear as an icon in the DHU's app launcher grid (Goal: "confirm
TankPilot appears in the Android Auto app launcher"). Tap it to launch
`TankPilotCarSession` → `TankPilotCarHomeScreen`.

---

## 9. Switching fixtures

**There is no developer/fixture selector inside the Android Auto UI itself** — this is
intentional (see the Important Safety Rule and Architecture Rules in
[phase-03a-android-auto-foundation.md](../phases/phase-03a-android-auto-foundation.md)).
All fixture switching happens on the **phone**, before or during a DHU session:

1. On the phone (not in the DHU window), open TankPilot normally.
2. From the Home screen, tap **TEST LAB** (only visible in debug builds —
   `BuildConfig.DEBUG`-gated in `HomeScreen.kt`).
3. Scroll to the **Android Auto (Desktop Head Unit)** section.
4. Tap one of the nine scenarios (`AA1`–`AA9`, see table below).
5. Switch back to the DHU window. The car's Fuel Status screen updates automatically —
   `TankPilotCarHomeScreen` and `FuelRescueRecommendationsScreen` both observe live
   `StateFlow`s and call `invalidate()` on change; no reconnect is needed.
6. Tap **Reset & Back** in Test Lab to return to production data (real vehicle/trip/
   fill-up numbers), or select a different `AA` scenario directly.

Selecting a scenario sets `TestFixtures.androidAutoScenarioOverrideEnabled = true`
plus the relevant `fuelScenario` / `stationScenario` / `confidenceScenario` values.
This flag exists **only** in `:testSupport`, is read **only** by
`DebugCarFuelPreviewProvider` and `DebugFuelRescueScenarioOverrideProvider` (both
`androidApp/src/debug`-only), and is compiled out of release entirely — release binds
`ReleaseCarFuelPreviewProvider` / `NoOpFuelRescueScenarioOverrideProvider`, which never
consult it. See §12 for how to verify this yourself.

### Fixture table

| Scenario | What it sets up | Expected result |
|---|---|---|
| **AA1 — Normal Fuel** | Half tank, all stations reachable | Fuel Status: Normal. Fuel Rescue: up to 3 labeled stations (Best Overall / Closest Safe / Cheapest Reachable). |
| **AA2 — Low Fuel** | Low-fuel threshold crossed | Fuel Status: Low Fuel. Fuel Rescue: same as AA1 (still reachable). |
| **AA3 — Critical, Safe Stations** | Critical fuel, nearby stations still in range | Fuel Status: Critical. Fuel Rescue: **still shows the recommendations list**, not the Critical screen — reachability, not fuel level alone, decides that hand-off. |
| **AA4 — Critical, No Safe Station** | Critical fuel, all stations placed far out of range | Fuel Rescue hands off to **Critical Fuel Screen**: "No station is safely within the current estimate," with Roadside Assistance / Open Nearest Station (Unconfirmed) / Return (header back). |
| **AA5 — Missing Prices** | Stations with no price data | Rows show "Price Unavailable" — never `$0.00`. A priceless station may appear as Closest Safe, never as Cheapest Reachable. |
| **AA6 — Stale Cached Prices** | All station prices >24h old | Every price is shown with a freshness label (e.g. "price may be outdated") — never a bare price with no age context. |
| **AA7 — Offline Station Data** | `MockFuelStationProvider` throws, simulating no network | Fuel Rescue shows no stations (no crash). Fuel Status is unaffected — it never touches station data. |
| **AA8 — No Fuel Estimate** | Fuel estimate itself withheld | Fuel Status shows "Unavailable" for every field — never `0%` / `0 gal`. |
| **AA9 — Invalid Station Coordinates** | Stations report `NaN` lat/lng | Station Detail **omits the Navigate action** entirely for these stations, rather than offering a non-functional one. |

---

## 10. What to check at each screen

- **Root screen loads**: `TankPilotCarHomeScreen` renders a `PaneTemplate` titled
  "TankPilot" (or "TankPilot (Preview)" while an `AA` scenario is active) within
  10 seconds of launch (`DR-2`/`DR-3` quality guidance).
- **Fuel Rescue navigation depth**: Home → Fuel Rescue Recommendations → Station Detail
  (depth 3), and Home → Fuel Rescue → Critical → Station Detail/Roadside Info (depth 4).
  Both are within `MAX_CAR_SCREEN_STACK_DEPTH = 5`
  (`androidApp/src/main/java/com/tankpilot/android/auto/CarNavigationLimits.kt`).
  Confirm the hardware/DHU back action always returns one level, never a dead end.
- **Debug fixtures switch scenarios**: selecting an `AA` scenario in Test Lab visibly
  changes the DHU screen within a couple of seconds, without needing to relaunch
  TankPilot in the car.
- **Release exposes no debug controls**: see §12 — this is verified by build
  inspection, not by tapping around a release build in the DHU (a release build won't
  have Test Lab at all, so there is nothing to tap).

---

## 11. Verification report template

Fill this in for your own session — DHU results are host-simulator results, not in-car
validation (see the notice at the top of this document).

```
Phone model:
Android version:
Android Auto (Gearhead) version:
DHU version:
Car App API level declared:
Root screen result:
Recommendation screen result:
Station detail result:
Navigation handoff result:
Offline result:
Critical state result:
Template depth result:
```

---

## 12. Confirming release exposes no debug controls (no DHU needed)

This is a build-inspection check, not a DHU session:

```bash
./gradlew :androidApp:assembleRelease
unzip -l androidApp/build/outputs/apk/release/androidApp-release-unsigned.apk | grep -i "testlab\|testsupport"
```

Expect **zero** matches. `TestLabScreen`/`TestLabViewModel` live in
`androidApp/src/debug` (excluded from the release source set entirely), and
`:testSupport` is only `debugImplementation` in `androidApp/build.gradle.kts` — it is
never on the release compile or runtime classpath, so nothing in `:testSupport`
(including `TestFixtures`) can even be referenced from release code, let alone shipped.

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| TankPilot doesn't appear in the DHU launcher | Unknown sources not enabled, or app not installed | Repeat §3; confirm with `adb shell pm list packages \| grep tankpilot`. |
| DHU window stays blank/black after connecting | Known DHU/host handshake issue | Close DHU, restart the head unit server on the phone (§5), relaunch DHU. Repeat once more if needed — this is a documented DHU quirk, not usually an app bug. |
| `adb forward` succeeds but DHU can't connect | Phone screen locked, or a stale forward from a previous session | Unlock the phone; `adb forward --remove-all` then re-run `adb forward tcp:5277 tcp:5277`. |
| `desktop-head-unit: command not found` | DHU not installed, or wrong directory | Re-check §1 — install via Android Studio SDK Manager, then `cd "$ANDROID_HOME/extras/google/auto"`. |
| Fuel Status shows stale data after switching `AA` scenarios | Rare — Test Lab and the car process weren't both pointed at the same app install | Force-stop and relaunch TankPilot on the phone, then reselect the scenario. |
| Android Auto app doesn't show a "Start head unit server" option | Developer mode not enabled | Repeat §2. |
| Device shows `unauthorized` in `adb devices` | USB debugging prompt not accepted on the phone | Check the phone screen for the RSA key confirmation dialog and accept it. |
