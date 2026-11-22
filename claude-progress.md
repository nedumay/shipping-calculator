# Project Progress

## Current status

- **Harness status:** Feature F007 Execution Complete.
- **Current task:** F007 Calculation Modes Implementation (Simple & Advanced).
- **Next action:** Done. All tasks in plan completed and verified.
- **Known blockers:** None.

## Project overview

### General Information
- **Project name:** Shipping calculator
- **Primary language:** Kotlin
- **Architecture:** Clean Architecture (Layered: UI -> ViewModel -> Domain -> Data)
- **UI Toolkit:** Jetpack Compose

### Modules
- **Android modules:** `:app` (Android Application)

### Tools and Versions
- **Kotlin Version:** 2.0.0
- **Android Gradle Plugin (AGP):** 8.5.0
- **Gradle Wrapper:** 9.3.1
- **Build System:** Kotlin DSL (`build.gradle.kts`)

### SDK and Configuration
- **minSdk:** 29 (Android 10)
- **targetSdk:** 34
- **compileSdk:** 34

### Architecture Details
- **State Management:** MVVM pattern (ViewModels managing UiState).
- **Dependency Injection:** Hilt
- **Persistence:** Room (via `AppDatabase.kt` and corresponding Entities/DAOs).
- **Asynchronous work:** Coroutines

## Baseline & Verification Results

| Check | Command | Result |
| :--- | :--- | :--- |
| Build | `:app:assembleDebug` | PASSED |
| Unit tests | `:app:testDebugUnitTest` | PASSED (2 passed) |

## Work log

### 2026-07-20 — Feature F007 Implementation: Calculation Modes
- **Goal:** Implement Simple vs. Advanced calculation modes in Shipping Calculator app.
- **Result:**
  - Added `CalculationMode` enum (`SIMPLE`, `ADVANCED`).
  - Added `calculationMode` property to `CalculatorUiState`.
  - Added `CalculationModeChanged` event to `CalculatorEvent`.
  - Updated `CalculatorViewModel.kt` to fallback to default vehicle parameters in `SIMPLE` mode when inputs are blank.
  - Added mode toggle chips and conditional card visibility in `CalculatorScreen.kt`.
  - Added localized strings in `values/strings.xml` and `values-ru/strings.xml`.
  - Created unit tests in `CalculateDeliveryCostUseCaseTest.kt`.
- **Files changed:**
  - `domain/model/CalculationMode.kt` [NEW]
  - `domain/model/ui/CalculatorUiState.kt`
  - `common/CalculatorEvent.kt`
  - `presentation/screen/main/CalculatorViewModel.kt`
  - `presentation/screen/main/CalculatorScreen.kt`
  - `values/strings.xml`
  - `values-ru/strings.xml`
  - `test/java/ru/nedumayy/shippingcalculator/domain/usecase/CalculateDeliveryCostUseCaseTest.kt` [NEW]
  - `feature_list.json`
- **Checks run:**
  - `:app:testDebugUnitTest` (2 tests passed)
  - `:app:assembleDebug` (build successful)
