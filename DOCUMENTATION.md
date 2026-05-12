# Documentation – APIs and libraries

This document gives a summary of API calls and libraries used in this project

## APIs

- **MET Locationforecast 2.0 (compact)**: `https://api.met.no/weatherapi/locationforecast/2.0/compact`. Point forecast (temperature, precipitation, wind) as JSON.
- **MET MetAlerts 2.0**: `https://api.met.no/weatherapi/metalerts/2.0/current.json`. Active weather warnings as GeoJSON, drawn as polygons on the map.
- **MET WMS (public-victoria)**: `https://public-victoria.met.no/wms`. Web Map Service for the MEPS, Arctic and ECMWF weather models; provides map tiles and a `GetCapabilities` document listing available layers.
- **Photon (Komoot) search**: `https://photon.komoot.io/api/`. Forward geocoding (place name → coordinates).
- **Photon (Komoot) reverse**: `https://photon.komoot.io/reverse`. Reverse geocoding (coordinates → place name).
- **Google Fused Location Provider**: Platform API from `play-services-location` for reading the device GPS position.

## Kotlin and Android foundation

- **androidx.core:core-ktx**: Kotlin extensions that make standard Android APIs more idiomatic.
- **androidx.lifecycle:lifecycle-runtime-ktx**: Managing UI state across Activity/Fragment.
- **androidx.activity:activity-compose**: Bridge between Android Activity and Jetpack Compose.
- **kotlin plugin.serialization**: Compiler plugin that enables automatic serialization of data classes.

## User Interface (Jetpack Compose)

- **androidx.compose:compose-bom**: Keeps all Compose libraries on compatible versions.
- **androidx.compose.ui**: Core library for building declarative user interfaces.
- **androidx.compose.ui:ui-tooling**: Tools for preview and debugging in Android Studio.
- **androidx.compose.material3**: Material Design 3 components for Compose.
- **androidx.compose.foundation**: Basic building blocks like layout, scrolling and gestures.
- **androidx.compose.foundation:foundation-layout**: Layout primitives like Row, Column, Box and padding modifiers.
- **androidx.compose.material:material-icons-extended**: Extended icon library for Material Design.
- **androidx.compose.ui:ui-text-google-fonts**: Loading of Google Fonts (used for the Actor typeface).

## Navigation

- **androidx.navigation:navigation-compose**: Type-safe navigation between Compose screens.
- **androidx.navigation:navigation-fragment / navigation-ui / navigation-dynamic-features-fragment**: Navigation support for fragment-based and dynamic modules.

## Map and position

- **org.osmdroid:osmdroid-android**: Map view based on OpenStreetMap.
- **com.google.android.gms:play-services-maps**: Google Maps SDK for Android.
- **com.google.android.gms:play-services-location**: Location Provider for retrieving the device's location.

## Network (Ktor)

- **io.ktor:ktor-bom**: Aligns versions across all Ktor modules.
- **io.ktor:ktor-client-core**: Core API for the Ktor HTTP client.
- **io.ktor:ktor-client-cio**: Coroutine-based network engine for Ktor.
- **io.ktor:ktor-client-content-negotiation**: Automatic (de)serialization of requests and responses based on content type.
- **io.ktor:ktor-serialization-kotlinx-json**: JSON plugin for Ktor that uses kotlinx.serialization.

## Serialization

- **kotlinx-serialization-json**: Kotlin-native JSON serialization (used for Locationforecast, MetAlerts and Photon).
- **kotlinx-serialization-core**: Runtime support for kotlinx.serialization.
- **xmlutil:serialization**: XML serialization compatible with kotlinx.serialization (used for WMS `GetCapabilities`).

## Pictures and logging

- **coil-compose**: Image loading with Compose integration.
- **coil-network-okhttp**: OkHttp-based network fetcher for Coil.
- **logback-classic**: SLF4J-compatible logging backend.

## Testing

- **junit**: Standard JUnit 4 framework for unit tests.
- **androidx.test.espresso:espresso-core**: Framework for UI testing of Android views.
- **androidx.compose.ui:ui-test-junit4**: JUnit 4 rules and helpers for testing Compose UI.
- **androidx.compose.ui:ui-test-manifest**: Manifest contribution that lets Compose UI tests run in debug builds.
- **com.google.truth**: Library for more readable assertions in tests.
- **kotlinx-coroutines-test**: Test utilities for coroutines.

## Build

- **com.android.application (AGP)**: Android Gradle Plugin for building the app.
- **org.jetbrains.kotlin.plugin.compose**: Compose compiler plugin.
