# Oasis Infobyte Summer Internship Program (OIBSIP)

Welcome to my repository for the **Oasis Infobyte Android App Development Internship**. 
This repository contains the projects I have developed as part of the internship tasks, built entirely from scratch using **Kotlin** and modern **Jetpack Compose**.

---

## 📱 Projects Overview

### [Task 1: Unit Converter App](./PremMotghare_Task01)
A versatile and highly responsive Unit Converter App designed to seamlessly convert values across multiple measurement categories.

**Key Features:**
- **Categories:** Supports Length, Weight, and Temperature conversions.
- **Real-time Conversion:** Instantaneous mathematical output as you type.
- **Intuitive UI:** Clean Material Design 3 interface utilizing bottom-sheet unit pickers for easy navigation.
- **Swap Functionality:** Instantly swap the "From" and "To" units with a single tap.
- **Clean Architecture:** Built using Compose state management, keeping the UI layout completely separate from the core conversion logic.

### [Task 2: Calculator App](./Task2-CalculatorApp)
A modern, fully-functional Calculator App featuring both basic arithmetic and advanced scientific calculations.

**Key Features:**
- **Dual Modes:** Smooth, animated toggle between Basic keypad (+, −, ×, ÷, %) and Scientific keypad (sin, cos, tan, √, x², ln, π, etc.).
- **Immediate Evaluation:** Scientific operations process instantly upon button press, streamlining the user workflow.
- **Robust State Management:** Powered by MVVM architecture and `StateFlow` to flawlessly manage live expressions, results, and calculation history.
- **Dynamic Layout:** Features a heavily optimized, responsive Compose layout grid utilizing adaptive weights, ensuring that the keypad scales beautifully to any screen dimension without overlapping.
- **Automated Testing & CI/CD:** Includes automated Espresso/Compose UI scripts for screen-recording simulations, and GitHub Actions workflows for continuous integration.

---

## 🛠️ Tech Stack & Architecture
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Design System:** Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **State Management:** StateFlow & Compose State
- **Testing:** JUnit4, Espresso, Compose UI Testing

---

## 🚀 How to Run the Projects Locally

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
   - **Important:** Do *not* open the root repository folder. Instead, specifically select either the **`PremMotghare_Task01`** folder (for Task 1) or the **`Task2-CalculatorApp`** folder (for Task 2) to open them as independent Android projects.

3. **Sync Gradle:**
   Once opened, Android Studio will automatically start syncing the dependencies. Wait for the loading bar at the bottom to finish. (If it asks you to sync, click the "Sync Project with Gradle Files" elephant icon in the top toolbar).

4. **Run the App:**
   - Select your target device (Emulator or physical phone) from the device dropdown menu at the top.
   - Click the green **Run (▶️)** button.
   - The app will compile and launch directly on your device!

---
*Developed by [Prem Motghare](https://github.com/Prem976421)*
