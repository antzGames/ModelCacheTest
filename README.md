# ModelCacheTest

Press P to cycle between Model Cache Test and Prime Number Test.

## Model Cache Test results:

See video: https://youtu.be/6qUZIZu6-AI

Desktop: 1.0x (used as a baseline)

GWT: 23.7x slower than desktop

gdx-teaVM: 8.8x slower than desktop

## Prime Number Test results:

See video: https://youtu.be/vMdqeVIcF6I

Desktop: 1.0x (used as a baseline)

GWT: 6.78x slower than desktop

gdx-teaVM: 1.24x slower than desktop

# Conclusions

GWT (the official libGDX web backend) generates JS code that performs extremely poorly.

gdx-teavm performs very well with java only code (no libGDX classes), but using libGDX classes it performs poorly.

What is it in the libGDX classes that is causing a 3-4x slowdown in both GWT and gdx-teavm? 

Note: Godot uses Emscripten to make web builds, and produces WASM (WebAssembly) code that performs on par with compiled C++ code.

If you are going to target the web, please do not use libGDX to make games.

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
