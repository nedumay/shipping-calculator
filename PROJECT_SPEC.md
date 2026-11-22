# Project Specification: Shipping Calculator

## 1. Project Overview
This document serves as the formal specification for the Shipping Calculator Android application. It outlines the purpose, target audience, and core features of the application.

### 1.1 Goals
The primary goal of the application is to provide a comprehensive tool for calculating and tracking estimated delivery costs for vehicles. It allows users to manage their personal vehicle fleet, calculate costs based on usage parameters, and maintain a record of vehicle maintenance.

### 1.2 Target Audience
Individual users or small businesses who require a simple, local tool to estimate shipping costs, track vehicle health, and manage operational data for a fleet of vehicles.

## 2. Technical Requirements

### 2.1 Platform and SDK
*   **Minimum SDK (minSdk):** 29 (Android 10)
*   **Target SDK (targetSdk):** 34
*   **Compile SDK (compileSdk):** 34
*   **Primary Language:** Kotlin
*   **Target Platform:** Android (Single Application Module: `:app`)

### 2.2 Technology Stack
*   **UI Framework:** Jetpack Compose
*   **Architecture:** Clean Architecture (MVVM pattern). Clear separation of concerns between Presentation, Domain, and Data layers.
*   **Database:** Room (SQLite abstraction) for local persistence of all core data (vehicles, calculations, settings, maintenance).
*   **State Management:** `UiState` managed by `ViewModel`s, utilizing Kotlin Coroutines for asynchronous operations.
*   **Dependency Injection:** Implemented via dedicated modules (Inferred Hilt usage).
*   **Navigation:** Implemented using Compose Navigation.

## 3. Core Functional Requirements (Derived from F001-F006)

| Feature ID | Name | Description | Status |
| :--- | :--- | :--- | :--- |
| **F001** | Vehicle Management | CRUD operations for user vehicles, including setting types, categories, and managing lists. | Complete |
| **F002** | Delivery Cost Calculation | Core feature to calculate estimated delivery costs based on vehicle type, distance, and fuel consumption. | Complete |
| **F003** | Maintenance Logging | Allow users to log maintenance records for their vehicles (e.g., oil changes, tire rotations). | Complete |
| **F004** | Calculation History | Save and retrieve historical delivery calculation results for later review. | Complete |
| **F005** | User Settings Management | Manage application-wide settings such as preferred fuel types or calculation preferences. | Complete |
| **F006** | Usage Analytics | Track and display statistics related to application usage and calculated data. | Complete |
| **F007** | Calculation Modes | Switch between Simple and Advanced calculation UI modes. | Complete |
| **F008** | Share Delivery Quote | Export and share formatted delivery cost receipts/commercial offers via Telegram, WhatsApp, or PDF. | Complete |

## 4. Constraints and Risks

*   **Scope Constraint:** All changes must strictly adhere to the existing Clean Architecture structure. No new architectural layers are permitted without explicit approval.
*   **Scope Constraint:** No refactoring of working code is allowed unless it is required to implement a new feature.
*   **Data Constraint:** Database schemas and public APIs of internal data classes must remain backward compatible.
*   **Technical Risk:** The absence of an identified external API for real-time data (e.g., fuel prices or distance APIs) means cost calculations are currently based on local, static data. Any external integration will require a formal dependency update and network implementation.

## 5. Definition of Done
A task is considered complete when:
1. The requested behavior is implemented.
2. Relevant acceptance criteria (from `feature_list.json`) are verified.
3. All appropriate checks (build, tests) pass.
4. The project documentation (`feature_list.json` and `claude-progress.md`) is updated to reflect the change.