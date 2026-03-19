import org.jetbrains.kotlin.gradle.internal.types.error.ErrorModuleDescriptor.platform

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.23"
}

android {
    namespace = "no.uio.ifi.in2000.dylansc.team6project"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.dylansc.team6project"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

//Disse verdiene settes opp kun for å gjøre det enklere å endre
//mange implementations på en gang, uten å måtte endre hver enkelt individuelt.

val kotlinx_version = "1.10.0"
val ktor_version = "3.4.0"
val coil_version = "3.3.0"

dependencies {
    implementation("io.coil-kt.coil3:coil-compose:${coil_version}")
    implementation("io.coil-kt.coil3:coil-network-okhttp:${coil_version}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-xml:${kotlinx_version}")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation(platform("io.ktor:ktor-bom:$ktor_version"))
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    implementation("io.ktor:ktor-client-core:${ktor_version}")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}