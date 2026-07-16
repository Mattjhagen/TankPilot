package com.tankpilot.fuelrescue.domain

/**
 * Configuration for the gas price API integration.
 * Values are injected via Koin — never hardcode API keys.
 */
data class GasPriceConfig(
    /** Base URL for the gas price API. */
    val apiBaseUrl: String = "https://api.collectapi.com/gasPrice",
    /** API key for authentication. Empty string disables API calls. */
    val apiKey: String = "",
    /** Default search radius in miles. */
    val searchRadiusMiles: Double = 10.0,
    /** Cache TTL in milliseconds (default 15 min). */
    val cacheTtlMs: Long = 900_000L,
    /** Maximum stations to fetch per request. */
    val maxResults: Int = 20
) {
    val isConfigured: Boolean get() = apiKey.isNotBlank()
}
