# Architecture & Development documentation

## Purpose and audience

For developers taking over the project and IN2000 sensors. Describes how the codebase is organised and why.

Coding style: standard Kotlin idioms; UI strings in `strings.xml`; most public classes and functions have KDoc.

## Application overview

Single-Activity Compose app with three screens (`onboarding`, `map`, `appInfo`) routed by `AppNavHost`. Calls four external services (MET WMS, Locationforecast, MetAlerts, Photon) through Ktor and renders the result on an OSMDroid map.

## Android API level

`minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`.

`minSdk = 24` is the lowest level with `java.time`, which `MapViewModel` uses for the WMS time slider. `targetSdk = 36` keeps the app compliant with current Play Store requirements.

## Folder structure

```
app/src/main/java/no/uio/ifi/in2000/dylansc/team6project/
├── App.kt                  Application class — builds the HttpClients
├── MainActivity.kt         Single Activity, hosts the Compose tree
├── data/                   Repositories + data sources + API config
│   ├── ApiConstants.kt
│   ├── HttpClientProvider.kt
│   ├── locationforecast/   Locationforecast API
│   ├── search/             Photon search API
│   ├── warning/            MetAlerts API
│   ├── weather/            WMS map layers
│   └── repository/         All repositories
├── model/domain/           Domain types (CurrentWeather, ClothingTips, WMSDomain)
└── ui/
    ├── AppNavHost.kt       Navigation + manual DI wiring
    ├── appinfo/            "About" screen
    ├── map/                Main screen + MapViewModel
    │   ├── components/     Smaller UI pieces, grouped by region
    │   └── util/           Location helpers
    ├── onboarding/         Carousel first time the app is used
    └── theme/              Compose theme
```

## Guidelines for future development

- **New screen:** create `ui/<feature>/` with `<Feature>Screen.kt`. Add a ViewModel only if the screen has async work or state that must survive rotation.
- **New API:** create `data/<apiname>/` with `XxxDataSource` (interface) + `XxxDataSourceImpl`. Add `XxxRepository` under `data/repository/`. Wire it in `AppNavHost.kt` and add the URL to `ApiConstants.kt`.

## Design pattern: MVVM

We use **MVVM**. State flows from the ViewModel down to the Composables via `StateFlow`; events flow back up as ViewModel function calls.

Why MVVM:
- In line with Compose's state-driven model.
- ViewModels survive configuration changes — `MapScreen` is recreated on rotation, but `MapViewModel` keeps the weather data and selected layer alive.
- Keeps Composables small and the ViewModel unit-testable (see `MapViewModelTest`).

Not every screen has a ViewModel, and this choice is deliberate. `AppInfoScreen` and `OnboardingCarousel` are stateless, so they don't need one. If a screen has real state or side effects, a ViewModel is needed.

## Future considerations for maintenance and further development

- **API URLs:** all in `ApiConstants.kt`.
- **HTTP client config:** in `HttpClientProvider.kt`. Two clients because WMS returns XML and the rest return JSON.
- **Caching:** `LocationRepository` caches WMS layers per `AreaData` in memory. There is no disk cache.
- **Animation lag:** the weather-layer animation can still lag, especially in the emulator.