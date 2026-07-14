import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

// Release signing reads from keystore.properties at the repo root, which is
// gitignored — never commit signing credentials. Absent locally (e.g. on a
// contributor's machine or CI without secrets configured), releaseSigning
// stays null and the release build type simply goes unsigned rather than
// failing the whole build.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val releaseSigningProperties = if (keystorePropertiesFile.exists()) {
    Properties().apply { load(keystorePropertiesFile.inputStream()) }
} else {
    null
}

android {
    namespace = "com.tankpilot.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tankpilot.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        if (releaseSigningProperties != null) {
            create("release") {
                storeFile = file(releaseSigningProperties.getProperty("storeFile"))
                storePassword = releaseSigningProperties.getProperty("storePassword")
                keyAlias = releaseSigningProperties.getProperty("keyAlias")
                keyPassword = releaseSigningProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (releaseSigningProperties != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.datetime)
    implementation(libs.serialization.json)
    
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.androidx.car.app)
    implementation(libs.androidx.car.app.projected)
    testImplementation(libs.androidx.car.app.testing)
    testImplementation(libs.junit)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(project(":testSupport"))
}
