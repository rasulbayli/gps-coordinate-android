# GPS Coordinate

A small native Android app for the Android lab assignment. It reads the device's live position from the GPS provider and displays latitude, longitude, and reported accuracy.

## Features

- Requests Android's precise location permission at runtime.
- Uses `LocationManager.GPS_PROVIDER` for satellite-derived coordinates.
- Updates the displayed position as new GPS fixes arrive.
- Explains when permission is missing, GPS is disabled, or a fix is still being acquired.
- No network access or background location permission is requested.

## Build and run

Open this folder in Android Studio and run the `app` configuration on a GPS-enabled device or emulator. Grant precise location when prompted and enable Location in Android settings. To simulate a GPS fix in the Android emulator, set a location in Extended Controls > Location.

## Permission setup

`ACCESS_FINE_LOCATION` is declared in `app/src/main/AndroidManifest.xml` and requested only when the user taps the location button. Location updates stop when the activity leaves the foreground.

## Screenshot

See [`outputs/gps-coordinate-demo.png`](outputs/gps-coordinate-demo.png) for the running app.

Android package name: ca.rasuldadashbayli.
