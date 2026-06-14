# Oasis Infobyte Summer Internship Program (OIBSIP)

Welcome to my repository for the **Oasis Infobyte Android App Development Internship**. 
This repository contains the projects I have developed as part of the internship tasks, built entirely from scratch using **Kotlin** and modern **Jetpack Compose**.

---

## Projects Overview

### [Task 1: Unit Converter App](./PremMotghare_Task01(Unit%20Converter))
A versatile and highly responsive Unit Converter App designed to seamlessly convert values across multiple measurement categories.

**Key Features:**
- **Categories:** Supports Length, Weight, and Temperature conversions.
- **Real-time Conversion:** Instantaneous mathematical output as you type.
- **Intuitive UI:** Clean Material Design 3 interface utilizing bottom-sheet unit pickers for easy navigation.
- **Swap Functionality:** Instantly swap the "From" and "To" units with a single tap.
- **Clean Architecture:** Built using Compose state management, keeping the UI layout completely separate from the core conversion logic.

### [Task 2: Calculator App](./PremMotghare_Task02(Calculator%20App))
A modern, fully-functional Calculator App featuring both basic arithmetic and advanced scientific calculations.

**Key Features:**
- **Dual Modes:** Smooth, animated toggle between Basic keypad (+, âˆ’, Ã—, Ã·, %) and Scientific keypad (sin, cos, tan, âˆš, xÂ², ln, Ï€, etc.).
- **Immediate Evaluation:** Scientific operations process instantly upon button press, streamlining the user workflow.
- **Robust State Management:** Powered by MVVM architecture and `StateFlow` to flawlessly manage live expressions, results, and calculation history.
- **Dynamic Layout:** Features a heavily optimized, responsive Compose layout grid utilizing adaptive weights, ensuring that the keypad scales beautifully to any screen dimension without overlapping.
- **Automated Testing & CI/CD:** Includes automated Espresso/Compose UI scripts for screen-recording simulations, and GitHub Actions workflows for continuous integration.

### [Task 3: Stopwatch & Time Manager](./PremMotghare_Task03(Stopwatch%20&%20Time%20Manager))
A modern, full-featured time management app containing a World Clock, Alarms, a Stopwatch, and a Timer.

**Key Features:**
- **World Clock:** Dynamic searchable dropdown to view the local time of major country capitals with flag emojis.
- **Smart Alarms:** Set exact, precision alarms using native Android `TimePickerDialog` with robust Android 14+ background permission handling.
- **Stopwatch with Laps:** A beautifully animated stopwatch that tracks milliseconds and records laps effortlessly.
- **Custom Timer:** Smooth vertical scrolling "Wheel Picker" built completely from scratch using Compose `LazyColumn` for seamless hour, minute, and second selection.
- **Aesthetic UI:** Fully built in Jetpack Compose utilizing Material Design 3 and a custom 4-color animated sweeping gradient.

---

## Tech Stack & Architecture

- **Jetpack Compose:** Completely moved away from legacy XML layouts. Built declarative, highly reusable UI components across all apps, ranging from responsive Calculator adaptive grids to the Unit Converter's modal bottom sheets, and the Stopwatch's infinite-scrolling Timer wheel.
- **Material Design 3 (Material You):** Implemented the latest Google design language. The UI adapts dynamically with modern color palettes, elevated surfaces, and rounded aesthetics for a premium user experience across every project.
- **MVVM Architecture (Model-View-ViewModel):** A strict separation of concerns. The ViewModels handle all complex business logic—whether calculating advanced scientific expressions, mapping real-time unit conversions, or managing background timer ticks—keeping the Compose UI layers lightweight and purely reactive.
- **StateFlow & Coroutines:** Leveraged Kotlin Coroutines for lightweight background threading and `StateFlow` to broadcast state changes flawlessly from the ViewModel to the UI, guaranteeing that the interface is always perfectly in sync with the data.
- **System APIs & Permissions:** Integrated deeply with native Android APIs and robust handling of Android 13/14+ runtime permissions.

---

## How to Run the Projects Locally

If you'd like to test out the applications on your own machine, follow these simple steps:

### Prerequisites
- Install the latest version of [Android Studio](https://developer.android.com/studio).
- Configure an Android Virtual Device (Emulator) in Android Studio, or enable USB debugging on a physical Android phone.

### Steps to Run
1. **Clone the Repository:**
   Open your terminal/command prompt and run:
   ```bash
   git clone https://github.com/Prem976421/OIBSIP.git
   ```

2. **Open the Project in Android Studio:**
   - Launch Android Studio.
   - Click on **Open**.
   - Navigate to the cloned repository directory.
   - **Important:** Do *not* open the root repository folder. Instead, specifically select either **`PremMotghare_Task01(Unit Converter)`**, **`PremMotghare_Task02(Calculator App)`**, or **`PremMotghare_Task03(Stopwatch & Time Manager)`** to open them as independent Android projects.

3. **Sync Gradle:**
   Once opened, Android Studio will automatically start syncing the dependencies. Wait for the loading bar at the bottom to finish. (If it asks you to sync, click the "Sync Project with Gradle Files" elephant icon in the top toolbar).

4. **Run the App:**
   - Select your target device (Emulator or physical phone) from the device dropdown menu at the top.
   - Click the green **Run (â–¶ï¸)** button.
   - The app will compile and launch directly on your device!

---
*Developed by [Prem Motghare](https://github.com/Prem976421)*  
**Let's Connect:** [LinkedIn](https://www.linkedin.com/in/prem-motghare-2372a41b5)

