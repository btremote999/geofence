Geofencing Android Demos
===================================

These are demos for the Geofencing assigment
- Add/Delete GeoFence from the map 
- Provide status In/Out with the following rules 
- Rule 1: A device is considered to be inside of the geofence area if the device remains geographically inside the defined circle. 
- Rule 2: the device is treated as being inside the geofence area, if device still connected to the specific Wifi network

Note: this source does not set the rule check on the specific Wifi Network 

Pre-requisites
--------------

- Target Android SDK v28
- Latest Android Build Tools
- Android Support Repository
- Google Repository
- Google Play services

Getting Started
---------------
This sample use the Gradle build system.

First download the samples by cloning this repository or downloading an archived
snapshot. (See the options at the top of the page.)

In Android Studio, use "Open an existing Android Studio project".
Next select the ApiDemos/java/ directory that you downloaded from this repository.
If prompted for a gradle configuration accept the default settings. 

Alternatively use the "gradlew build" command to build the project directly.

Add your API key to the AndroidManifest.xml for <meta-data> android:name="com.google.android.geo.API_KEY"> </meta-data>
It's pulled from there into your app's `AndroidManifest.xml` file.
See the [quick guide to getting an API key](https://developers.google.com/maps/documentation/android-api/signup).


Test Setting
-------------
The request change the Location Setting Mode to "High accuracy", else it may the geofence monitor provide the error with status code 1000 (GEOFENCE_NOT_AVAILABLE)
Demo video available at /demo directory
