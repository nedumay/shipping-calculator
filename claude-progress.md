# Project Progress

## Current status

- **Harness status:** Simple Calculation Mode Logic Refactored.
- **Current task:** Simple Calculation Mode Logic Simplification.
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
| Unit tests | `:app:testDebugUnitTest` | PASSED (4 passed) |

## Work log

### 2026-07-20 — Simple Mode Logic Refactoring
- **Goal:** Make Simple calculation mode calculate purely based on user inputs without hidden background presets.
- **Result:**
  - Updated `CalculateDeliveryCostUseCase.kt` to handle `0.0` values safely without throwing requirement exceptions.
  - Updated `CalculatorViewModel.kt` so unentered fields in Simple Mode evaluate to `0.0` (excluded from calculation) instead of substituting category presets.
  - Added unit test `calculateCost_unfilledOptionalFields_returnsZeroCostForUnfilled` in `CalculateDeliveryCostUseCaseTest.kt`.
- **Files changed:**
  - `domain/usecase/CalculateDeliveryCostUseCase.kt`
  - `presentation/screen/main/CalculatorViewModel.kt`
  - `test/java/ru/nedumayy/shippingcalculator/domain/usecase/CalculateDeliveryCostUseCaseTest.kt`
- **Checks run:**
  - `:app:testDebugUnitTest` (4 tests passed)
  - `:app:assembleDebug` (build successful)

### 2026-07-20 — Feature F008 Implementation: Share Delivery Quote
- **Goal:** Implement delivery quote sharing functionality for Telegram, WhatsApp, SMS, and other apps.
- **Result:**
  - Created `ShareQuoteUtils` utility object with quote formatting and Android Share Intent trigger.
  - Added string resources for quote headers, route, distance, total, and watermark in English and Russian.
  - Added "Поделиться сметой" button to `ResultCard` in `CalculatorScreen.kt`.
  - Added Share icon action to `HistoryItem` in `HistoryScreen.kt`.
  - Created unit tests in `ShareQuoteUtilsTest.kt`.

### 2026-07-20 — Feature F007 Implementation: Calculation Modes
- **Goal:** Implement Simple vs. Advanced calculation modes in Shipping Calculator app.
- **Result:**
  - Added `CalculationMode` enum (`SIMPLE`, `ADVANCED`).
  - Added `calculationMode` property to `CalculatorUiState`.
  - Added `CalculationModeChanged` event to `CalculatorEvent`.
  - Added mode toggle chips and conditional card visibility in `CalculatorScreen.kt`.
