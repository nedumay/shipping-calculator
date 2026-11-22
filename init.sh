#!/usr/bin/env bash
#
# Android Kotlin Agent Harness - environment diagnostics
#
# Run from the repository root:
#   ./init.sh
#
# This script is intentionally read-only. It does not install SDKs,
# modify Gradle files, download dependencies, or change local configuration.

set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

section() {
  printf '\n%s\n' "============================================================"
  printf '%s\n' "$1"
  printf '%s\n' "============================================================"
}

info() { printf '[INFO] %s\n' "$1"; }
warn() { printf '[WARN] %s\n' "$1"; }
fail() { printf '[FAIL] %s\n' "$1"; }

section "Android Kotlin Harness: environment"

info "Repository: $ROOT_DIR"
info "Date: $(date 2>/dev/null || printf 'unavailable')"

section "Java"
if command -v java >/dev/null 2>&1; then
  java -version 2>&1
else
  warn "Java is not available on PATH."
fi

section "Android SDK environment"
if [ -n "${ANDROID_HOME:-}" ]; then
  info "ANDROID_HOME=$ANDROID_HOME"
  [ -d "$ANDROID_HOME" ] || warn "ANDROID_HOME does not point to an existing directory."
else
  warn "ANDROID_HOME is not set."
fi

if [ -n "${ANDROID_SDK_ROOT:-}" ]; then
  info "ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
  [ -d "$ANDROID_SDK_ROOT" ] || warn "ANDROID_SDK_ROOT does not point to an existing directory."
else
  warn "ANDROID_SDK_ROOT is not set."
fi

section "Gradle wrapper"
if [ -f "./gradlew" ]; then
  if [ -x "./gradlew" ]; then
    info "Gradle wrapper found and executable."
  else
    warn "gradlew exists but is not executable. On Unix/macOS run: chmod +x gradlew"
  fi

  info "Gradle wrapper version:"
  if [ -x "./gradlew" ]; then
    ./gradlew --version
  else
    bash ./gradlew --version
  fi
else
  fail "No ./gradlew found. Run this script from the Android repository root."
fi

section "Project structure"
for path in \
  settings.gradle.kts settings.gradle \
  build.gradle.kts build.gradle \
  gradle/libs.versions.toml \
  gradle/wrapper/gradle-wrapper.properties \
  local.properties \
  app/build.gradle.kts app/build.gradle \
  AGENTS.md feature_list.json claude-progress.md
do
  if [ -f "$path" ]; then
    info "Found: $path"
  fi
done

section "Android modules (best-effort)"
if [ -f "settings.gradle.kts" ] || [ -f "settings.gradle" ]; then
  info "Module declarations should be inspected in settings.gradle(.kts)."
else
  warn "Gradle settings file not found at repository root."
fi

section "Harness files"
for path in AGENTS.md feature_list.json claude-progress.md init.sh; do
  if [ -f "$path" ]; then
    info "Found: $path"
  else
    warn "Missing: $path"
  fi
done

section "Next steps"
cat <<'NEXT'
1. Ask the agent to inspect the repository and read AGENTS.md.
2. Complete the bootstrap entries in feature_list.json.
3. Record the discovered architecture and baseline checks in claude-progress.md.
4. Only then start implementing product features.
NEXT

section "Diagnostics complete"
info "This script does not guarantee that the Android SDK, emulator, or dependencies are fully configured."
