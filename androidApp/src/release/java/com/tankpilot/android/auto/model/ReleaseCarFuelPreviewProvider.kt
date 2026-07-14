package com.tankpilot.android.auto.model

/**
 * Release builds never fabricate fuel data. If no vehicle is configured, the
 * Android Auto root screen shows "Unavailable" rather than a fixture.
 */
class ReleaseCarFuelPreviewProvider : CarFuelPreviewProvider {
    override fun previewSnapshot(): CarFuelSnapshot? = null
}
