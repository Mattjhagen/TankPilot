plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.datetime)
            implementation(libs.koin.core)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.serialization.json)
        }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.core)
            implementation(project(":testSupport"))
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.driver.android)
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.driver.native)
        }
    }
}

android {
    namespace = "com.tankpilot"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

sqldelight {
    databases {
        create("TankPilotDb") {
            packageName.set("com.tankpilot.db")
        }
    }
}
