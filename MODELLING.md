# Modelling

We have added three use case descriptions:
- Switch between weather layers
- Clothing tip based on weather conditions on a specific location
- Toggle weather alerts

We will show these as use textual desciptions, use case diagrams and sequence diagrams. Lastly we have included a class diagram.

## Functional requirements
The top-five functional requirements are listed in the table below:

| ID  | Requirement                            | Description                                                                                                                                                                                                  |
| --- | -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| FR1 | Weather layers on the map              | The app let's the user toggle between temperature, precipitation and wind. The selected layer is rendered as WMS tiles overlay.                                                                              |
| FR2 | Weather alerts overlay and description | The system fetches active weather alerts and display them as polygons. Tapping a polygon opens details about the weather alert.                                                                              |
| FR3 | Weather and clothing tip on long-press | When the user taps on a specific point on the map, the system fetches current weather (temperature, wind speed, rainfall) and showcases it together with a clothing suggestion based on the weather values. |
| FR4 | Clothing tip explanation               | The system should be able to show an explanation to the user which weather condition (temperature range, rainfall, wind) triggered the displayed clothing tip.                                               |
| FR5 | Forecast layer navigation              | The system shall let the user step through the overlayforecast in time via a slider, and update the displayed layer to match the selected timestamp.                                                         |

## Use case 1: Visualise weather layers on the map
**Actor:** User

**Goal:** Visualize different weather data on the map (temperature, precipitation, wind)

**Precondition**: The map screen is open, theWMS data source is reachable and internet is connected

**Main flow:**
1. User opens the bottom menu
2. System lists available layers from the current data source
3. User selects the desired layer
4. System sets up a WMS overlay and displays the layer name in the info badge

**Alternative flow:**

4a. Tiles fail to load → system displays an error card with "Try again"

### Use case diagram


### Sequence diagram
```mermaid
sequenceDiagram
    actor User
    participant UI as MapScreen
    participant VM as MapViewModel
    participant Repo as LocationRepository
    participant WMS as WMS API

    Note over VM, Repo: On startup
    VM->>Repo: getArea(area)
    Repo->>WMS: fetchWmsCapabilities()
    WMS-->>Repo: WMSCapabilities
    Repo-->>VM: List(WMSLayer)
    VM-->>UI: uiState.displayLayers updated

    User->>UI: Opens bottom menu and selects layer
    UI->>VM: setSelectedLayer(layer)
    VM-->>UI: uiState.selectedLayer updated

    alt Tiles do not load
        UI-->>User: Shows error card with Try Again
    else Tiles load
        UI->>UI: Sets up TilesOverlay with WMS source
        UI-->>User: Map loads and displays tiles automatically
    end
```

### Activity diagram
```mermaid
flowchart TD
    Start([Start]) --> A[User opens bottom menu]
    A --> B[System lists available layers from current data source]
    B --> C[User selects desired layer]
    C --> D[System requests WMS tiles for selected layer]
    D --> E{Tiles load successfully?}
    E -->|Yes| F[Set up TilesOverlay with WMS source]
    F --> G[Display layer name in info badge]
    G --> End([End])
    E -->|No| H[Show error card with 'Try again']
    H --> I{User taps 'Try again'?}
    I -->|Yes| D
    I -->|No| End
```

## Use case 2: User views weather alerts
**Actor**: User

**Goal**: See active weather alerts as an overlay on the map

**Precondition**: The map screen is open

**Main flow:**
1. User opens the bottom menu and taps "Farevarsler"
2. System enables the alert overlay
3. System draws color-coded polygons on the map based on already fetched alerts
4. The first time the user enables alerts, a hint banner is displayed
5. User taps a polygon
6. System displays MapDangerWarningInfo with details about the alert

**Alternative flow:**

2a. No active alerts → no polygons are drawn


```mermaid
sequenceDiagram
    actor User
    participant UI as MapScreen
    participant VM as MapViewModel
    participant Repo as AlertRepository
    participant API as MET Alerts API

    Note over VM, Repo: On startup
    VM->>Repo: getAlertList()
    Repo->>API: alertDataSource()

    alt No active weather alerts
        API-->>Repo: Empty list
        Repo-->>VM: emptyList()
        VM-->>UI: uiState.alertList is empty
    else Weather alerts are available
        API-->>Repo: List(AlertFeature)
        Repo-->>VM: List(AlertFeature)
        VM-->>UI: uiState.alertList updated
    end

    User->>UI: Opens bottom menu and pushes "Weather Alerts" button
    UI->>VM: toggledangerAlert()
    VM-->>UI: uiState.dangerAlert updated
    UI->>UI: drawAlerts() draws color-coded polygons on the map
    UI-->>User: Shows a hint banner the first time

    User->>UI: Clicks on a polygon
    UI->>VM: onAlertClick(feature)
    VM-->>UI: uiState.selectedAlert updated
    UI-->>User: Displays MapDangerWarningInfo with event, severity etc.
```

## Use case 3: User gets clothing suggestions
**Actor**: User

**Goal**: Get a clothing suggestion based on weather conditions at a selected location

**Precondition** The map screen is open, internet connected

**Main flow:**
1. User long-presses a point on the map
2. System fetches weather data for the selected point
3. System displays a dialog with temperature, precipitation and wind speed, along with a clothing tip based on the conditions
4. User taps the info button next to the clothing tip
5. System shows an explanation of which weather conditions triggered the tip

### Use case diagram

### Sequence diagram

```mermaid
sequenceDiagram
    actor User
    participant UI as MapScreen
    participant VM as MapViewModel
    participant Repo as WeatherRepository
    participant API as Locationforecast API

    User->>UI: Long-presses a point on the map
    UI->>VM: onLocationSelected(lat, lon)
    VM->>Repo: getCurrentWeather(lat, lon)
    Repo->>API: getForecast(lat, lon)
    API-->>Repo: Forecast data
    Repo-->>VM: CurrentWeather
    VM-->>UI: uiState.currentWeather updated
    UI-->>User: Displays weather data and clothing tip

    User->>UI: Taps the info button
    UI-->>User: Displays explanation of the clothing tip
```

## Class diagram

```mermaid
classDiagram
    class MapScreen {
        +observeUiState()
        +drawAlerts()
        +onLongPress(lat, lon)
    }

    class MapViewModel {
        -locationRepo : LocationRepository
        -alertRepo : WeatherAlertRepository
        -weatherRepo : WeatherRepository
        +uiState : StateFlow~MapScreenUiState~
        +setSelectedLayer(layer)
        +toggledangerAlert()
        +onAlertClick(feature)
        +onLocationSelected(lat, lon)
        +dismissCurrentWeather()
    }

    class MapScreenUiState {
        +displayLayers : List
        +selectedLayer : WMSLayer
        +alertList : List~AlertFeature~
        +dangerAlert : Boolean
        +selectedAlert : AlertFeature
        +currentWeather : CurrentWeather
    }

    class LocationRepository {
        -wmsDataSource : WMSDataSource
        +getArea(area) List~WMSLayer~
    }

    class WeatherAlertRepository {
        -warningDataSource : AlertDataSource
        +getAlertList() List~AlertFeature~
    }

    class WeatherRepository {
        -weatherDataSource : LocationforecastDataSource
        +getCurrentWeather(lat, lon) CurrentWeather
    }

    class WMSDataSource {
        <<interface>>
        +fetchWmsCapabilities(model) WMSCapabilities
    }

    class AlertDataSource {
        <<interface>>
        +alertDataSource() List~AlertFeature~
    }

    class LocationforecastDataSource {
        <<interface>>
        +getForecast(lat, lon) LocationforecastResponse
    }

    class WMSCapabilities {
        +capability : Capability
    }

    class WMSLayer {
        +name : String
        +title : String
        +dimension : String
    }

    class AlertFeature {
        +geometry : AlertGeometry
        +properties : AlertProperties
    }

    class CurrentWeather {
        +latitude : Double
        +longitude : Double
        +temperature : Double
        +windSpeed : Double
        +rainfall : Double
    }

    class ClothingTip {
        <<enumeration>>
        +emoji : String
        +label : String
        +condition : String
        +getClothingTip(weather) ClothingTip
    }

    MapScreen --> MapViewModel : observes uiState
    MapViewModel --> MapScreenUiState : exposes
    MapViewModel --> LocationRepository : uses
    MapViewModel --> WeatherAlertRepository : uses
    MapViewModel --> WeatherRepository : uses
    LocationRepository --> WMSDataSource : uses
    WeatherAlertRepository --> AlertDataSource : uses
    WeatherRepository --> LocationforecastDataSource : uses
    WMSDataSource ..> WMSCapabilities : returns
    WMSCapabilities "1" *-- "*" WMSLayer : contains
    AlertDataSource ..> AlertFeature : returns
    WeatherRepository ..> CurrentWeather : returns
    ClothingTip ..> CurrentWeather : derived from
    MapScreenUiState "1" o-- "*" WMSLayer : displayLayers / selectedLayer
    MapScreenUiState "1" o-- "*" AlertFeature : alertList / selectedAlert
    MapScreenUiState "1" o-- "0..1" CurrentWeather : currentWeather
```
