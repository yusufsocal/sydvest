# Dokumentasjon – API-er og biblioteker

This document gives a summary of API-calls and libraries used in the project.

## APIs

All base URLs are collected in `data/ApiConstants.kt`. HTTP calls go through Ktor clients built in `HttpClientProvider`.

- **MET Locationforecast 2.0 (compact)** — `https://api.met.no/weatherapi/locationforecast/2.0/compact`. Point forecast (temperature, precipitation, wind) as JSON. Used by `LocationforecastDataSource`. Requires a `User-Agent` header.
- **MET MetAlerts 2.0** — `https://api.met.no/weatherapi/metalerts/2.0/current.json`. Active hazard warnings as GeoJSON. Used by `AlertDataSource` and drawn as polygons on the map.
- **MET WMS (public-victoria)** — `https://public-victoria.met.no/wms`. Web Map Service for the MEPS, Arctic and ECMWF models. We call `GetCapabilities` (XML) via `WMSDataSource`; map tiles are fetched directly by osmdroid.
- **Photon (Komoot) search** — `https://photon.komoot.io/api/`. Forward geocoding (place name → coordinates). Used by `SearchDataSource.fetchSearchSuggestions`.
- **Photon (Komoot) reverse** — `https://photon.komoot.io/reverse`. Reverse geocoding (coordinates → place name). Used by `SearchDataSource.findplaceNameFromCoordinates`.
- **Google Fused Location Provider** — platform API (not HTTP) from `play-services-location`. Used in `MapUtils` to read the device GPS position.

## Kotlin and Android foundation

- **androidx.core:core-ktx** – Kotlin-utvidelser som gjør standard Android-API-er mer idiomatiske.
- **androidx.lifecycle:lifecycle-runtime-ktx** – Livssyklusbevisste komponenter for å håndtere UI-tilstand på tvers av Activity/Fragment.
- **androidx.activity:activity-compose** – Bindeledd mellom Android `Activity` og Jetpack Compose.
- **kotlin plugin.serialization** – Kompilator-plugin som gjør det mulig å serialisere data-klasser automatisk.

## User Interface (Jetpack Compose)

- **androidx.compose:compose-bom** – Holder alle Compose-bibliotek på kompatible versjoner.
- **androidx.compose.ui** – Kjernebiblioteket for å bygge deklarative brukergrensesnitt.
- **androidx.compose.ui:ui-tooling** – Verktøy for forhåndsvisning og feilsøking i Android Studio.
- **androidx.compose.material3** – Material Design 3-komponenter for Compose.
- **androidx.compose.foundation** – Grunnleggende byggeklosser som layout, scrolling og gester.
- **androidx.compose.foundation:foundation-layout** – Layout-primitiver som `Row`, `Column`, `Box` og padding-modifikatorer.
- **androidx.compose.material:material-icons-extended** – Utvidet ikonbibliotek for Material Design.
- **androidx.compose.ui:ui-text-google-fonts** – Lasting av Google Fonts (brukes i `Type.kt` for skrifttypen *Actor*).

## Navigation

- **androidx.navigation:navigation-compose** – Typesikker navigasjon mellom Compose-skjermer.
- **androidx.navigation:navigation-fragment / navigation-ui / navigation-dynamic-features-fragment** – Navigasjonsstøtte for fragmentbaserte og dynamiske moduler.

## Map and position

- **org.osmdroid:osmdroid-android** – Kartvisning basert på OpenStreetMap (selve `MapView` i appen).
- **com.google.android.gms:play-services-maps** – Google Maps SDK for Android.
- **com.google.android.gms:play-services-location** – Fused Location Provider for å hente enhetens geoposisjon.

## Network (Ktor)

- **io.ktor:ktor-bom** – Samordner versjoner for alle Ktor-moduler.
- **io.ktor:ktor-client-core** – Kjerne-API for HTTP-klienten i Ktor.
- **io.ktor:ktor-client-cio** – Coroutine-basert nettverksmotor for Ktor.
- **io.ktor:ktor-client-content-negotiation** – Automatisk (de)serialisering av forespørsler og svar basert på innholdstype.
- **io.ktor:ktor-serialization-kotlinx-json** – JSON-plugin for Ktor som bruker kotlinx.serialization.

## Serialization

- **kotlinx-serialization-json** – Kotlin-native JSON-serialisering (brukes for Locationforecast, MetAlerts og Photon).
- **kotlinx-serialization-core** – Kjøretidsstøtte for kotlinx.serialization.
- **xmlutil:serialization** – XML-serialisering kompatibel med kotlinx.serialization (brukes for WMS `GetCapabilities`).

## Pictures and logging

- **coil-compose** – Bildelasting med Compose-integrasjon.
- **coil-network-okhttp** – OkHttp-basert nettverkshenter for Coil.
- **logback-classic** – SLF4J-kompatibel logging-backend.

## Testing

- **junit** – Standard JUnit 4-rammeverk for enhetstester.
- **androidx.test.espresso:espresso-core** – Rammeverk for UI-testing av Android-views.
- **androidx.compose.ui:ui-test-junit4** – JUnit 4-regler og -hjelpere for å teste Compose-UI.
- **androidx.compose.ui:ui-test-manifest** – Manifest-bidrag som lar Compose-UI-tester kjøre i debug-bygget.
- **com.google.truth** – Bibliotek for mer lesbare assertions i tester.
- **kotlinx-coroutines-test** – Testverktøy for coroutines.

## Build

- **com.android.application (AGP)** – Android Gradle Plugin for å bygge appen.
- **org.jetbrains.kotlin.plugin.compose** – Compose-kompilator-plugin.