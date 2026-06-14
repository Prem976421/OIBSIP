# StopWatch App - Task 03

This repository contains the StopWatch Android application developed as part of Task 03 for the Oasis InfoByte Summer Internship Program (OIBSIP). 

## Project Overview

The StopWatch App is a beautifully designed, modern Android application built using Jetpack Compose. It allows users to track time efficiently with a clean, responsive UI featuring deep indigo and slate blue aesthetics.

### Key Features
- **Start/Pause/Resume**: Accurately control time tracking.
- **Laps**: Record and display individual lap times alongside total elapsed time.
- **Reset**: Quickly clear the current session.
- **Modern UI**: Built with Jetpack Compose using Material Design 3 guidelines and a custom color palette.

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel) utilizing `StateFlow` and Coroutines for precise time ticking.
- **Minimum SDK**: 24

## Documentation & Code Structure

- **`StopwatchViewModel.kt`**: Contains the core logic for the stopwatch, handling the `Job` that ticks every 10ms to provide a highly responsive `StateFlow` to the UI. It calculates time differences to prevent lag and ensure accuracy.
- **`StopwatchScreen.kt`**: The Compose UI layer that reactively updates based on the ViewModel's state. It includes a custom `LazyColumn` for displaying laps and elegantly styled rounded buttons.

## Submission Details

- **Intern Name**: Prem Motghare
- **Task**: Task 03 (Stopwatch App)
- **Live Demo**: [Insert Link Here]
- **Repository Link**: [Insert GitHub Repo Link Here]

### Build Instructions
To build and run the project locally:
1. Open the project in Android Studio.
2. Sync the Gradle files.
3. Run the app on an emulator or a physical device.
