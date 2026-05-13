# SydVest
![Logo](app/src/main/sydvest_logo-playstore.png)

SydVest is an Android application that shows weather data. The app is developed in the course "Software Engineering with Project Work" (IN2000) at UiO.

## Team 6:
The app is made by:

- Dylan Coury
- Peder Hornseth Nygaard
- Mari Landsverk
- Yusuf Selim Ocal
- Nora Brenn Egge
- Silje Bechmann Granås

## In this app you can:
- Get weather data in the form of visual weather layers on the world map
- Get clothing tips based on the current weather
- Get current weather warnings for Norway
- See what the weather (rainfall, temperature and wind) is right now, anywhere in the world
- Search for locations you want to see weather data for

## Requirements
- An internet connection is required, as all weather data is fetched from live APIs.
- The app requests **location permission** in order to show the weather at your current position. You can deny the permission and still use the rest of the app.

## How to run the app
To run the app, you need [Android Studio](https://developer.android.com/studio) (the recommended IDE) with JDK 11 or higher. The project targets Android 7.0 (API level 24) and above, and the included Gradle wrapper will automatically download the correct Gradle version (9.1.0).

There are two options for getting the source code:
1) Download the zip file and unpack it.
2) Clone this repository from the command line: 
`git clone https://github.uio.no/IN2000-V26/team-6.git`

Then open the project folder in Android Studio and run it on an emulator or a connected Android device.

‼️ For the best experience with the weather overlay animations, we recommend running the app on a physical Android device. The emulator will display the overlays as well, but with slower performance and some lag.

## Additional info and documentation
If you want to continue working on SydVest, this project contains documents that give insight into the app's functionality, architecture, and resources.

- `ARCHITECTURE.md` contains a description of the app's architecture and structure.
- `MODELLING.md` contains the textual and visual descriptions of the app's functionality and structure.
- `DOCUMENTATION.md` contains descriptions of the libraries and APIs that the app uses.

## Attribution
Weather data and warnings are provided by [MET Norway](https://www.met.no/) under the [Norwegian Licence for Open Government Data (NLOD) 2.0](https://data.norge.no/nlod/en/2.0).


