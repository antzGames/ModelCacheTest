# ModelCacheTest

Press P to cycle between ModelCacheTest and PrimeNumber Test.

## Model Cache Test results:

Desktop: 1.0x (used a baseline)
GWT: 23.7x slower than desktop
teaVM: 8.8x slower than desktop

## Prime Number Test results:

Desktop: 1.0x (used a baseline)
GWT: 6.78x slower than desktop
teaVM:  1.24x slower than desktop

# Conclusions

GWT (the official libGDX web backend) generates JS code that performs extremely poorly.
gdx-teavm performs very well with java only code (no libGDX classes), but using libGDX classes it also performs poorly.

Note: Godot uses Emscripten to make web builds, and produces WASM (WebAssembly) code that performs on par as C++ code.

If you are going to target the web, please do not use libGDX.

See: https://www.youtube.com/playlist?list=PLQxIUTu_M7Z_5rwPRWBOjEzACH-f2_yX6

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `html`: Web platform using GWT and WebGL. Supports only Java projects.
- `teavm`: Experimental web platform using TeaVM and WebGL.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `html:dist`: compiles GWT sources. The compiled application can be found at `html/build/dist`: you can use any HTTP server to deploy it.
- `lwjgl3:run`: starts the application on desktop.
- `teavm:run`: serves the JavaScript application at http://localhost:8080 via a local Jetty server.
