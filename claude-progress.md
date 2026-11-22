# Project Progress

## Current status

- **Harness status:** Initializing/Executing repository discovery.
- **Current task:** Complete repository structure and configuration analysis (Phase 1).
- **Next action:** Await user confirmation before starting implementation of new features.
- **Known blockers:** None recorded.

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
- **Dependency Injection:** Present (Inferred Hilt usage via `di/` modules).
- **Persistence:** Room (via `AppDatabase.kt` and corresponding Entities/DAOs).
- **Networking:** Not detected in current analysis.
- **Asynchronous work:** Coroutines (Standard for modern Kotlin Android development).

## Baseline

| Check | Command | Result |
| :--- | :--- | :--- |
| Build | Not run | Pending discovery |
| Unit tests | Not run | Pending discovery |
| Lint / static analysis | Not run | Pending discovery |

## Issues and Risks

> [!WARNING]
> **Missing Networking Implementation:** The current project structure does not reveal any networking dependencies or services (e.g., Retrofit). Any feature requiring external APIs will necessitate the introduction of new dependencies and configuration.
>
> [!IMPORTANT]
> **Architectural Consistency:** The project follows established Clean Architecture principles. I will strictly preserve this structure and only make changes directly related to the requested feature scope.

## Active work

No product feature is active yet.

## Decisions and constraints

- Preserve the existing architecture unless a change is explicitly requested by the user.
- Preserve the configured `minSdk` unless the user approves changing it.
- Prefer small, verifiable changes.
- Do not claim a check passed unless it completed successfully.

## Work log

### 2026-07-20 — Repository Discovery Completion
- **Goal:** Analyze and document the project structure, technologies, and configurations.
- **Result:** Project structure and tech stack documented in this file. Initial assessment confirms a Clean Architecture based on MVVM/Compose, utilizing Room and modern Kotlin features.
- **Files/modules changed:** `claude-progress.md`
- **Checks run:** None (Initial discovery).
- **Known issues/blockers:** None.
- **Next action:** Await user instruction for the first feature implementation.
