# Fly Mod

A simple Fabric client-side flight mod for Minecraft 26.3.

## Controls

- **G** — toggle flight
- **WASD** — move relative to the camera
- **Space** — rise
- **Shift** — descend

## Build

This project follows the current Fabric 26.3 project layout.

GitHub Actions installs Gradle directly and runs:

```text
gradle build
```

The compiled JAR is uploaded as a workflow artifact from `build/libs/`.
