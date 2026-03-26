plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
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
val ktor_version = "3.0.0"
val nav_version = "2.9.7"

dependencies {
    implementation("androidx.navigation:navigation-compose:${nav_version}")
    implementation("androidx.navigation:navigation-fragment:${nav_version}")
    implementation("androidx.navigation:navigation-ui:${nav_version}")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:${nav_version}")

    // Kun selve XML-motoren
    implementation("io.github.pdvrieze.xmlutil:serialization:0.90.1")

    // Standard Ktor 3 (uten XML-plugins)
    implementation("io.ktor:ktor-client-core:3.0.0")
    implementation("io.ktor:ktor-client-cio:3.0.0")

    // Coil & Logging
    implementation("io.coil-kt.coil3:coil-compose:3.0.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0")
    implementation("ch.qos.logback:logback-classic:1.2.11")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation(platform("io.ktor:ktor-bom:$ktor_version"))
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    implementation("io.ktor:ktor-client-core:${ktor_version}")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_version")



    // AndroidX / Compose (behold dine eksisterende libs.androidx her)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}