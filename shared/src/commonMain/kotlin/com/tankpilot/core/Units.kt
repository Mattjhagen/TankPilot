package com.tankpilot.core

import kotlin.jvm.JvmInline

@JvmInline
value class Gallons(val value: Double) {
    init {
        require(value.isFinite() && value >= 0.0) { "Gallons must be positive and finite: $value" }
    }

    fun toLiters(): Liters = Liters(value * 3.785411784)
    operator fun plus(other: Gallons): Gallons = Gallons(value + other.value)
    operator fun minus(other: Gallons): Gallons = Gallons(maxOf(0.0, value - other.value))
    operator fun compareTo(other: Gallons): Int = value.compareTo(other.value)
}

@JvmInline
value class Liters(val value: Double) {
    init {
        require(value.isFinite() && value >= 0.0) { "Liters must be positive and finite: $value" }
    }

    fun toGallons(): Gallons = Gallons(value / 3.785411784)
    operator fun plus(other: Liters): Liters = Liters(value + other.value)
    operator fun minus(other: Liters): Liters = Liters(maxOf(0.0, value - other.value))
    operator fun compareTo(other: Liters): Int = value.compareTo(other.value)
}

@JvmInline
value class Miles(val value: Double) {
    init {
        require(value.isFinite() && value >= 0.0) { "Miles must be positive and finite: $value" }
    }

    fun toKilometers(): Kilometers = Kilometers(value * 1.609344)
    operator fun plus(other: Miles): Miles = Miles(value + other.value)
    operator fun minus(other: Miles): Miles = Miles(maxOf(0.0, value - other.value))
    operator fun compareTo(other: Miles): Int = value.compareTo(other.value)
}

@JvmInline
value class Kilometers(val value: Double) {
    init {
        require(value.isFinite() && value >= 0.0) { "Kilometers must be positive and finite: $value" }
    }

    fun toMiles(): Miles = Miles(value / 1.609344)
    operator fun plus(other: Kilometers): Kilometers = Kilometers(value + other.value)
    operator fun minus(other: Kilometers): Kilometers = Kilometers(maxOf(0.0, value - other.value))
    operator fun compareTo(other: Kilometers): Int = value.compareTo(other.value)
}

@JvmInline
value class MilesPerGallon(val value: Double) {
    init {
        require(value.isFinite() && value > 0.0) { "MPG must be positive, finite, and non-zero: $value" }
    }

    fun toLitersPer100Km(): LitersPer100Km = LitersPer100Km(235.214583 / value)
}

@JvmInline
value class LitersPer100Km(val value: Double) {
    init {
        require(value.isFinite() && value > 0.0) { "L/100km must be positive, finite, and non-zero: $value" }
    }

    fun toMilesPerGallon(): MilesPerGallon = MilesPerGallon(235.214583 / value)
}

@JvmInline
value class CurrencyMicros(val value: Long) {
    init {
        require(value >= 0L) { "Currency micros must be non-negative: $value" }
    }

    operator fun plus(other: CurrencyMicros): CurrencyMicros = CurrencyMicros(value + other.value)
    operator fun minus(other: CurrencyMicros): CurrencyMicros = CurrencyMicros(maxOf(0L, value - other.value))
    operator fun times(factor: Double): CurrencyMicros = CurrencyMicros((value * factor).toLong())
    operator fun compareTo(other: CurrencyMicros): Int = value.compareTo(other.value)
}

data class Money(
    val amountMicros: CurrencyMicros,
    val currencyCode: String
) {
    operator fun plus(other: Money): Money {
        require(currencyCode == other.currencyCode) { "Currency mismatch: $currencyCode vs ${other.currencyCode}" }
        return Money(amountMicros + other.amountMicros, currencyCode)
    }

    operator fun minus(other: Money): Money {
        require(currencyCode == other.currencyCode) { "Currency mismatch: $currencyCode vs ${other.currencyCode}" }
        return Money(amountMicros - other.amountMicros, currencyCode)
    }

    operator fun times(factor: Double): Money = Money(amountMicros * factor, currencyCode)
}

enum class FuelPriceUnit {
    PER_GALLON,
    PER_LITER
}

data class FuelPricePerUnit(
    val money: Money,
    val unit: FuelPriceUnit
) {
    fun toPerGallon(): FuelPricePerUnit {
        return when (unit) {
            FuelPriceUnit.PER_GALLON -> this
            FuelPriceUnit.PER_LITER -> {
                // 1 Gallon = 3.785411784 Liters
                val amountPerGallon = (money.amountMicros.value * 3.785411784).toLong()
                FuelPricePerUnit(
                    Money(CurrencyMicros(amountPerGallon), money.currencyCode),
                    FuelPriceUnit.PER_GALLON
                )
            }
        }
    }
}
