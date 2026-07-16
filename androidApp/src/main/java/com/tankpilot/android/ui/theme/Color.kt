package com.tankpilot.android.ui.theme

import androidx.compose.ui.graphics.Color

// ── Tesla-Inspired Base Palette ──────────────────────────────────
val TeslaDarkBg = Color(0xFF0A0A0C)          // Near-black background
val TeslaDarkSurface = Color(0xFF111113)      // Card/surface background
val TeslaDarkElevated = Color(0xFF1A1A1E)     // Elevated surfaces
val TeslaGlassBg = Color(0x14FFFFFF)          // 8% white — glassmorphic
val TeslaGlassBorder = Color(0x1AFFFFFF)      // 10% white — glass border

// ── Typography Colors ────────────────────────────────────────────
val TeslaWhite = Color(0xFFFFFFFF)
val TeslaGrayPrimary = Color(0xFFB0B0B8)      // Secondary text
val TeslaGraySecondary = Color(0xFF6B7280)     // Tertiary/label text
val TeslaGrayMuted = Color(0xFF3A3A40)         // Muted/disabled

// ── Accent Colors ────────────────────────────────────────────────
val TeslaBlue = Color(0xFF3B82F6)             // Electric blue — active/good states
val TeslaBlueDim = Color(0xFF1D4ED8)          // Blue variant — pressed/deep
val TeslaBlueGlow = Color(0x333B82F6)         // Blue glow for OBD connected state
val TeslaCyan = Color(0xFF22D3EE)             // Cyan accent

// ── Fuel State Colors (semantic) ─────────────────────────────────
val FuelFull = Color(0xFF22C55E)              // Green — fuel above 50%
val FuelGood = Color(0xFF4ADE80)              // Light green — fuel 25-50%
val FuelLow = Color(0xFFF59E0B)              // Amber — fuel below 25%
val FuelCritical = Color(0xFFEF4444)          // Red — critical/reserve
val FuelEmptyImminent = Color(0xFFDC2626)     // Bright red — empty imminent

// ── Legacy compatibility (existing components reference these) ───
val DarkBg = TeslaDarkBg
val DarkSurface = TeslaDarkSurface
val White = TeslaWhite
val GraySecondary = TeslaGraySecondary
val GrayBorder = TeslaGrayMuted

val FuelGreen = FuelFull
val FuelLime = FuelGood
val FuelYellow = FuelLow
val FuelOrange = Color(0xFFFF9F0A)
val FuelRed = FuelCritical

// ── Driving Type Colors ──────────────────────────────────────────
val CityDriving = Color(0xFF8B5CF6)           // Purple — city
val HighwayDriving = TeslaBlue                // Blue — highway
val MixedDriving = TeslaGrayPrimary           // Gray — mixed

// ── MPG Indicator Colors ─────────────────────────────────────────
val MpgGood = Color(0xFF22C55E)               // Above average
val MpgAverage = TeslaGrayPrimary             // Average
val MpgPoor = Color(0xFFF59E0B)               // Below average

