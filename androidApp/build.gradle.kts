import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

// Release signing reads from keystore.properties at the repo root, which is
// gitignored — never commit signing credentials.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val releaseSigningProperties = if (keystorePropertiesFile.exists()) {
    Properties().apply { load(keystorePropertiesFile.inputStream()) }
} else {
    val isReleaseTask = gradle.startParameter.taskNames.any {
        it.contains("release", ignoreCase = true)
    }
    if (isReleaseTask) {
        throw GradleException(
            "Release build aborted: 'keystore.properties' is missing at the repository root.\n" +
            "Please create or restore 'keystore.properties' containing release signing parameters (storeFile, storePassword, keyAlias, keyPassword) to build signed release bundles."
        )
    }
    null
}

android {
    namespace = "com.tankpilot.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tankpilot.android"
        minSdk = 26
        targetSdk = 35
        // Versioning scheme: MAJOR*10000 + MINOR*100 + PATCH, e.g. 1.0.0=10000,
        // 1.0.1=10001, 1.1.0=10100, 2.0.0=20000. Leaves room within each minor
        // version for 100 patch releases before colliding with the next minor.
        versionCode = 10000
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
    implementation("com.google.android.gms:play-services-location:21.3.0")
    testImplementation(libs.androidx.car.app.testing)
    testImplementation(libs.junit)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(project(":testSupport"))
}
