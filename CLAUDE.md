# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Legado (阅读) is an open-source Android novel/e-book reader. It supports custom book sources (web scraping rules), local TXT/EPUB files, RSS subscriptions, and a built-in web server for remote management.

## Build Commands

```bash
# Build debug APK (output: app/build/outputs/apk/app/debug/)
./gradlew assembleAppDebug

# Build release APK (requires signing config in gradle.properties)
./gradlew assembleAppRelease

# Build all variants
./gradlew assembleApp

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Web module (Vue 3 frontend in modules/web)
cd modules/web && pnpm dev
```

## Architecture

Multi-module Gradle project (Gradle 8.13, JDK 17, AGP, Kotlin):

- **`:app`** - Main Android application (`io.legado.app`)
- **`:modules:book`** - EPUB parsing library (`me.ag2s`)
- **`:modules:rhino`** - Mozilla Rhino JS engine wrapper (`com.script`)
- **`modules/web`** - Vue 3 web UI for bookshelf and source editing (separate build, embedded as assets)

### App Module Structure (`app/src/main/java/io/legado/app/`)

| Package | Purpose |
|---------|---------|
| `api` | Content Provider + Web API endpoints |
| `base` | Base classes (activities, fragments, adapters) |
| `data` | Room database: DAOs + entities (Book, BookChapter, BookSource, RssSource, ReplaceRule, etc.) |
| `help` | Utility/helper classes (crypto, storage, config) |
| `lib` | Vendored libraries (dialogs, icu4j, permissions, theme, webdav) |
| `model` | Business logic: `analyzeRule` (source rule parsing), `localBook` (local file parsing), `rss`, `webBook` (network fetching) |
| `service` | Android services: audio playback, TTS, source checking, download caching, web server |
| `ui` | All UI screens organized by feature (book/read, book/search, config, rss, etc.) |
| `web` | Embedded NanoHTTPD web server |

### Key Patterns

- **Database**: Room with KSP (not kapt). Schema exports to `app/schemas/`. Entities include Book, BookChapter, BookSource, RssSource, ReplaceRule.
- **UI**: Traditional Android Views with ViewBinding (no Compose). Activities/Fragments in `ui/`.
- **Networking**: OkHttp + Cronet. Glide for image loading with custom modules.
- **Book source rules**: Parsed by `model/analyzeRule/` using Jsoup, JsoupXpath, JsonPath, and Rhino JS engine.
- **Events**: LiveEventBus for intra-app communication.
- **Versioning**: `3.<yy>.<MMddHH>` format, versionCode = 10000 + git commit count.

### Build Variants

- **Flavor**: `app` (default)
- **Build types**: `debug` (suffix `.debug`), `release` (suffix `.release`, minified with ProGuard)
- **Signing**: Configured via `gradle.properties` properties (`RELEASE_STORE_FILE`, etc.)
- **APK naming**: `legado_<flavor>_<version>.apk`

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- `test.yml` - Builds on push to master, publishes beta pre-release
- `release.yml` - Manual dispatch for production release + Google Play upload
- `web.yml` - Builds the Vue 3 web module
- `cronet.yml` - Cronet library updates

## API

Two API interfaces (see `api.md`):
1. **Web API** (NanoHTTPD on port 1234/1235) - REST + WebSocket for book/source management
2. **Content Provider** - `io.legado.app.release.readerProvider` for inter-app communication
