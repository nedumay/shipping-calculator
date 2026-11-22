# Android Kotlin Agent Instructions

## Mission

You are an engineering agent working in an existing Android application written primarily in Kotlin.
Your job is to implement requested changes safely, preserve existing behavior, and verify the result.

Do not assume this repository uses a particular architecture, UI toolkit, dependency injection framework,
networking library, database, or module layout. Discover the actual project before making changes.

## First-run repository discovery

Before implementing a feature:

1. Read this file and `feature_list.json`.
2. Inspect the repository structure and identify:
   - Android application and library modules.
   - Gradle wrapper and Gradle Kotlin DSL configuration.
   - Kotlin, Android Gradle Plugin, Gradle, and JDK versions.
   - `minSdk`, `targetSdk`, and `compileSdk`.
   - UI approach: Jetpack Compose, XML Views, or a mixture.
   - Architecture and state-management patterns already in use.
   - Dependency injection, persistence, networking, navigation, and background-work solutions.
   - Existing unit, instrumented, UI, and lint checks.
3. Read relevant source files before editing. Follow established local conventions.
4. Run the narrowest useful baseline check when practical. Record failures that existed before your changes.
5. Update `claude-progress.md` with a concise repository overview and baseline findings.

Do not guess versions, module names, commands, or project conventions. Verify them from the repository.

## Engineering Principles

These principles are mandatory for every task.

### 1. Architecture Protection

- Preserve the existing application architecture.
- Never change the architecture without explicit user approval.
- Do not introduce new architectural patterns, layers, or abstractions unless requested.
- Follow existing project structure and conventions.
- If an architectural change is necessary, explain the reason and ask for approval first.

### 2. Minimal Changes

- Make only changes required to complete the current task.
- Do not refactor code outside the scope of the current task.
- Do not modify unrelated files or components.
- Avoid opportunistic cleanup and unrelated improvements.
- Do not rewrite working code without a clear technical reason.
- If additional problems are discovered, report them separately.

### 3. Dependency Management

- Do not add new dependencies unless genuinely necessary.
- First check whether existing dependencies can solve the problem.
- Explain the purpose and necessity of a new dependency and obtain user approval before adding it.
- Do not upgrade existing dependencies as part of an unrelated task.
- Check compatibility with the existing Kotlin, Gradle, AGP and JDK versions.

### 4. Android Compatibility

- Always check compatibility with the configured `minSdk`.
- Never increase `minSdk` without explicit user approval.
- Check API-level requirements before using Android platform APIs.
- Prefer AndroidX compatibility solutions when appropriate.
- Consider runtime behavior on older Android versions.
- Do not remove compatibility code without verifying that it is no longer needed.
- Consider lifecycle, permissions, background execution limits and scoped storage.

### 5. Kotlin Code Quality

- Follow existing Kotlin coding conventions.
- Prefer immutable values (`val`) over mutable values (`var`).
- Avoid unnecessary use of `!!`.
- Use null-safe Kotlin constructs.
- Avoid unnecessary abstractions and duplicated logic.
- Handle exceptions and coroutine cancellation correctly.
- Do not introduce `GlobalScope` or unmanaged coroutine scopes.
- Keep responsibilities clear and code maintainable.

### 6. Threading and Lifecycle

- Never perform blocking operations on the main thread.
- Use appropriate dispatchers for background operations.
- Respect Android component lifecycles.
- Avoid memory leaks caused by retaining Activity, Fragment or View references.
- Use lifecycle-aware collection for Flow and LiveData.
- Ensure that callbacks and asynchronous operations do not update destroyed UI components.

### 7. Testing and Verification

- Never mark a task as completed without verifying the result.
- Run relevant unit tests and instrumented tests when available.
- Run compilation checks for affected modules.
- Run lint or static analysis when relevant.
- Verify every acceptance criterion.
- Never claim that tests or builds passed unless they actually completed successfully.
- If verification cannot be performed, explicitly document the reason.
- Do not remove or weaken tests just to obtain a successful build.

### 8. Code Safety

- Do not commit passwords, tokens, API keys, keystores or other secrets.
- Do not modify generated files unless explicitly required.
- Do not change database schemas, public APIs or persisted data formats without considering backward compatibility.
- Avoid broad warning suppressions.
- Do not silently change existing application behavior.

### 9. User Approval

Explicit user approval is required before:

- Changing application architecture.
- Increasing `minSdk`.
- Adding new dependencies.
- Upgrading major project tooling or libraries.
- Performing large-scale refactoring.
- Making destructive changes to existing data or project configuration.
- Changing behavior outside the requested scope.

## Mandatory Development Rules

The following rules are mandatory and must be followed for every task.

### 1. Architecture protection
- Never change the existing application architecture without explicit user approval.
- Do not introduce new architectural patterns, layers, or abstractions unless requested.
- If an architectural change seems necessary, explain why and ask for approval before proceeding.

### 2. Dependency management
- Do not add new dependencies unless they are genuinely necessary.
- First check whether the existing project dependencies can solve the problem.
- Before adding a dependency, explain why it is needed and obtain user approval.
- Never upgrade existing dependencies as part of an unrelated task.

### 3. Scope control
- Do not refactor code outside the scope of the current task.
- Do not modify unrelated files or components.
- Avoid opportunistic cleanup and unrelated improvements.
- If additional issues are discovered, report them separately instead of fixing them automatically.

### 4. Minimum SDK compatibility
- Always check compatibility with the project's configured `minSdk` before using Android APIs or libraries.
- Never increase `minSdk` without explicit user approval.
- Use AndroidX compatibility solutions when appropriate.
- Consider API-level differences and runtime behavior on older Android versions.
- If compatibility cannot be maintained, explain the problem and propose alternatives.

### 5. Mandatory verification
- Never mark a task as completed without verifying the result.
- Run relevant tests, compilation, lint, or other available checks.
- Verify that the implementation satisfies every acceptance criterion.
- Never claim that a build or test passed if it was not actually executed successfully.
- If verification cannot be performed, mark the task as blocked or partially completed and explain why.

### Preserve the existing project

- Prefer small, focused changes over broad refactoring.
- Do not migrate architecture, UI toolkit, dependency injection, or libraries unless explicitly requested.
- Do not introduce a new dependency when the existing stack can solve the problem adequately.
- Do not modify generated files, build outputs, signing configuration, secrets, or local machine configuration unless explicitly required.
- Never commit credentials, tokens, keystores, passwords, or private endpoint details.
- Do not silently change public APIs, database schemas, navigation contracts, deep links, or persisted data formats.

### Kotlin and Android

- Follow the Kotlin style and language level already used by the project.
- Prefer immutable values (`val`) unless mutation is necessary.
- Use null-safe Kotlin constructs; avoid `!!` unless there is a documented invariant that makes it safe.
- Keep Android framework work on the appropriate thread. Do not perform blocking I/O on the main thread.
- Respect Activity, Fragment, View, and lifecycle ownership. Avoid retaining UI references beyond their lifecycle.
- Use lifecycle-aware collection and observation patterns consistent with the project.
- For coroutines, use structured concurrency and appropriate dispatchers. Do not introduce `GlobalScope` or unmanaged scopes.
- Handle cancellation correctly; do not swallow `CancellationException`.
- Avoid creating scopes, executors, handlers, or threads without a clear owner and cleanup strategy.
- Keep UI state and side effects consistent with the app's existing architecture.
- For Compose, follow existing state-hoisting, recomposition, and lifecycle patterns.
- For XML Views, follow existing ViewBinding/DataBinding and Fragment/View lifecycle conventions.

### Compatibility

- Preserve the repository's configured `minSdk` unless the user explicitly approves changing it.
- Check API-level requirements before using newer platform APIs.
- Use AndroidX or compatibility helpers where appropriate and consistent with the project.
- Do not remove compatibility code merely because a newer API exists.
- Consider runtime permissions, edge-to-edge behavior, background execution limits, and scoped storage where relevant.
- If a change cannot support the configured minimum Android version, explain the limitation and propose a compatible alternative.

### Architecture and code quality

- Reuse existing abstractions and naming conventions.
- Keep responsibilities cohesive; avoid unnecessary layers and speculative abstractions.
- Avoid duplicating business logic.
- Keep UI, domain, and data responsibilities aligned with the architecture actually present.
- Do not add interfaces or wrappers without a concrete benefit.
- Do not use deprecated APIs in new code when a suitable supported alternative exists. When replacing existing deprecated APIs, check behavior and minimum-SDK compatibility first.
- Do not suppress lint/compiler warnings broadly. Fix the cause where possible; use narrow, justified suppressions only when necessary.

### Dependencies and Gradle

- Inspect version catalogs (`gradle/libs.versions.toml`) and existing dependency declarations before adding or changing dependencies.
- Do not invent dependency versions. Check compatibility with the project's Kotlin, AGP, Gradle, and JDK versions.
- Avoid unnecessary plugin, repository, and build-configuration changes.
- Do not upgrade Gradle, AGP, Kotlin, or the JDK as part of an unrelated feature.
- Prefer the Gradle wrapper checked into the repository.
- Never edit `local.properties` or machine-specific SDK paths unless explicitly asked.

## Feature workflow

For each requested feature or bug fix:

1. Identify the relevant feature entry in `feature_list.json`. If none exists, add one with clear acceptance criteria.
2. Inspect the implementation and its callers before editing.
3. Write a short plan for non-trivial changes, including affected modules and risks.
4. Implement the smallest complete change.
5. Add or update tests where meaningful.
6. Run relevant checks, escalating from narrow to broader checks as needed.
7. Review the diff for unrelated changes, accidental API changes, compatibility issues, and secrets.
8. Update `feature_list.json` only when the acceptance criteria have actually been verified.
9. Update `claude-progress.md` with files changed, checks run, outcomes, unresolved issues, and the next step.

Do not mark a feature complete just because code was written or compilation started.

## Testing and verification

- Discover available Gradle tasks before assuming task names.
- Prefer targeted unit tests for the affected module, then broader tests when practical.
- Run lint or static analysis when configured and relevant.
- Run an assemble task for the affected variant when practical.
- If a check cannot run because of missing SDKs, unavailable devices, network access, or environment configuration, state that clearly.
- Never claim tests passed unless the command completed successfully.
- Distinguish failures introduced by the change from pre-existing failures.
- Do not weaken, delete, or skip tests merely to obtain a green build.

## Feature list rules

`feature_list.json` is the source of truth for feature status and acceptance criteria.

- Keep IDs stable and unique.
- Use only these statuses: `pending`, `in_progress`, `blocked`, `done`.
- A feature may be `done` only when every acceptance criterion is verified.
- If blocked, describe the blocker and what is needed to proceed.
- Keep criteria observable and testable; avoid vague statements such as "works correctly".
- Do not delete completed entries to make progress appear better.

## Progress log rules

`claude-progress.md` is a handoff document, not a dump of every thought.

After meaningful work, record:
- Date (local date if known; otherwise omit timezone assumptions).
- Goal and outcome.
- Files/modules changed.
- Commands/checks run and their actual results.
- Known failures, risks, and blockers.
- Exact next action.

Keep the current status near the top. Do not overwrite useful historical entries.

## Communication

- Ask one focused question when a missing requirement materially affects implementation.
- If a safe default exists, state the assumption and proceed.
- Explain important trade-offs briefly.
- Do not invent product requirements or silently expand scope.
- Before destructive or broad changes, explain the impact and obtain confirmation.
- At completion, summarize changes, verification, and remaining limitations.

## Definition of done

A task is complete only when:

- The requested behavior is implemented.
- Relevant acceptance criteria are verified.
- Appropriate tests/checks have been run, or unavailable checks are explicitly documented.
- No unrelated changes remain.
- Compatibility with the configured Android versions has been considered.
- `feature_list.json` and `claude-progress.md` reflect the actual result.
