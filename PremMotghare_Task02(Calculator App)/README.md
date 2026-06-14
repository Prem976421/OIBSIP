# 🧮 Task 2 — Calculator App

> **OASIS Infobyte Internship — Android Development Task 2**

A feature-rich Android Calculator app built with **Kotlin + Jetpack Compose** and **Material Design 3**.

---

## 📱 Features

| Feature | Details |
|---|---|
| **Basic Calculator** | Addition, Subtraction, Multiplication, Division |
| **Scientific Mode** | sin, cos, tan, log, ln, √, x², x³, π, e, n!, 1/x |
| **Mode Toggle** | Smooth animated switch between Basic ↔ Scientific |
| **Unit Converter** | Length, Weight, Temperature with 20+ units |
| **Expression Display** | Shows full expression + result simultaneously |
| **Delete Key** | Backspace with icon button |
| **History** | Last 20 calculations saved during session |

## 🎨 Design

- **Material Design 3** (Material You)
- **Light mode** with Blue-Indigo color palette
- **Press animations** on every button (scale effect)
- **Bottom navigation** between Calculator and Unit Converter
- **Animated mode transition** when switching Basic ↔ Scientific

## 🏗️ Architecture

```
app/
└── src/main/java/com/example/calculatorapp/
    ├── MainActivity.kt              ← Navigation host + BottomNavBar
    ├── ui/
    │   ├── theme/                   ← Material3 colors, typography, theme
    │   ├── components/
    │   │   └── CalcButton.kt        ← Reusable animated button
    │   └── screens/
    │       ├── CalculatorScreen.kt  ← Basic + Scientific keypad
    │       └── UnitConverterScreen.kt
    └── viewmodel/
        ├── CalculatorViewModel.kt   ← Expression evaluator + state
        └── UnitConverterViewModel.kt
```

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + StateFlow
- **Navigation**: Navigation Compose
- **Min SDK**: 24 (Android 7.0+)
- **Target SDK**: 36
- **Build Tool**: Gradle with Version Catalog (libs.versions.toml)

## 🚀 CI/CD

This project uses **GitHub Actions** for continuous integration:

- ✅ Triggers on every push to `main`
- ✅ Builds debug APK
- ✅ Runs unit tests
- ✅ Uploads APK as downloadable artifact

[![Android CI](https://github.com/Prem976421/OIBSIP/actions/workflows/calculator-ci.yml/badge.svg)](https://github.com/Prem976421/OIBSIP/actions/workflows/calculator-ci.yml)

## 📦 Building

```bash
# Debug build
./gradlew assembleDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean assembleDebug
```

---

*Part of the [OIBSIP](https://github.com/Prem976421/OIBSIP) internship project collection.*
