# Dokumentasjon – Biblioteker

Dette dokumentet gir en kort oversikt over bibliotekene som brukes i prosjektet, gruppert etter formål.

## Kotlin og Android-grunnlag

- **androidx.core:core-ktx** – Kotlin-utvidelser som gjør standard Android-API-er mer idiomatiske.
- **androidx.lifecycle:lifecycle-runtime-ktx** – Livssyklusbevisste komponenter for å håndtere UI-tilstand på tvers av Activity/Fragment.
- **androidx.activity:activity-compose** – Bindeledd mellom Android `Activity` og Jetpack Compose.
- **kotlin plugin.serialization** – Kompilator-plugin som gjør det mulig å serialisere data-klasser automatisk.

## Brukergrensesnitt (Jetpack Compose)

- **androidx.compose:compose-bom** – Holder alle Compose-bibliotek på kompatible versjoner.
- **androidx.compose.ui** – Kjernebiblioteket for å bygge deklarative brukergrensesnitt.
- **androidx.compose.ui:ui-graphics** – Grafikkprimitiver som farger, former og Canvas.
- **androidx.compose.ui:ui-tooling / ui-tooling-preview** – Verktøy for forhåndsvisning og feilsøking i Android Studio.
- **androidx.compose.material3** – Material Design 3-komponenter for Compose.
- **androidx.compose.foundation** – Grunnleggende byggeklosser som layout, scrolling og gester.
- **androidx.compose.material:material-icons-extended** – Utvidet ikonbibliotek for Material Design.

## Navigasjon

- **androidx.navigation:navigation-compose** – Typesikker navigasjon mellom Compose-skjermer.
- **androidx.navigation:navigation-fragment / navigation-ui / navigation-dynamic-features-fragment** – Navigasjonsstøtte for fragmentbaserte og dynamiske moduler.

## Kart og posisjon

- **org.osmdroid:osmdroid-android** – Kartvisning basert på OpenStreetMap.
- **com.google.android.gms:play-services-maps** – Google Maps SDK for Android.
- **com.google.android.gms:play-services-location** – Fused Location Provider for å hente enhetens geoposisjon.

## Nettverk (Ktor)

- **io.ktor:ktor-bom** – Samordner versjoner for alle Ktor-moduler.
- **io.ktor:ktor-client-core** – Kjerne-API for HTTP-klienten i Ktor.
- **io.ktor:ktor-client-cio** – Coroutine-basert nettverksmotor for Ktor.
- **io.ktor:ktor-client-content-negotiation** – Automatisk (de)serialisering av forespørsler og svar basert på innholdstype.
- **io.ktor:ktor-serialization-kotlinx-json** – JSON-plugin for Ktor som bruker kotlinx.serialization.

## Serialisering

- **kotlinx-serialization-json** – Kotlin-native JSON-serialisering.
- **kotlinx-serialization-core** – Kjøretidsstøtte for kotlinx.serialization.
- **xmlutil:serialization** – XML-serialisering kompatibel med kotlinx.serialization.

## Bilder og logging

- **coil-compose** – Bildelasting med Compose-integrasjon.
- **coil-network-okhttp** – OkHttp-basert nettverkshenter for Coil.
- **logback-classic** – SLF4J-kompatibel logging-backend.

## Testing

- **junit** – Standard JUnit 4-rammeverk for enhetstester.
- **androidx.test.ext:junit** – AndroidX-utvidelser for instrumenterte tester.
- **androidx.test.espresso:espresso-core** – Rammeverk for UI-testing av Android-views.
- **com.google.truth** – Bibliotek for mer lesbare assertions i tester.
- **kotlinx-coroutines-test** – Testverktøy for coroutines.

## Bygg

- **com.android.application (AGP)** – Android Gradle Plugin for å bygge appen.
- **org.jetbrains.kotlin.plugin.compose** – Compose-kompilator-plugin.