import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "lv.yumm"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = file ("keystore/release_store") // Path to your keystore file
            storePassword = "parole"
            keyAlias = "releaseKey"
            keyPassword = "parole"
        }
    }

    defaultConfig {
        applicationId = "lv.yumm"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true

            isShrinkResources = true

            applicationIdSuffix = ".release"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.9.3"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material3)
    implementation (libs.timber)
    implementation (libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.kotlinx.serialization.json)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    androidTestImplementation(libs.room.testing)
    testImplementation (libs.mockito.core)
    testImplementation (libs.turbine)
    ksp(libs.room.compiler)
    implementation(libs.hilt.android.core)
    implementation(libs.hilt.android.testing)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.ext.compiler)
    implementation(libs.hilt.ext.work)
    implementation(libs.gson)
    implementation (libs.androidx.work.runtime)
    implementation(libs.androidx.material)
    implementation (libs.accompanist.permissions)
    implementation (libs.androidx.foundation)
    // retrofit
    implementation (libs.retrofit)
    implementation (libs.logging.interceptor)
    implementation(libs.converter.scalars)
    // gson converter
    implementation (libs.converter.gson)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

kapt {
    correctErrorTypes = true
}