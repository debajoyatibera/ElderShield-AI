# ElderShield AI

ElderShield AI helps elderly users identify whether a call, link, or message is genuine or a scam. It analyzes suspicious communications and warns users using extremely simple language and a highly visible threat interface.

## Current Phase

**Phase 1 — Android Foundation & Accessible UI**

## Technology

- Kotlin
- Jetpack Compose
- Android (Min SDK 26)

*Note: Firebase and Gemini functionality are NOT implemented yet.*

## Architecture

The project conceptually follows Clean Architecture principles:
- **UI / Jetpack Compose:** High contrast, large typography accessible screens (Home, CheckMessage, ThreatResult).
- **Domain:** Clean abstractions for `ThreatAnalysisResult` and `ThreatAnalysisService`. No direct coupling to a specific AI backend.
- **Data/Services:** Placeholders for Phase 2 implementation.

## Current Capabilities

- Fully accessible UI shell for elderly users.
- Red Alert UI foundation for scam warnings.
- Simple manual checking interface.
- Localized structure (English, Bengali, Hindi).

## Future Phases

1. Communication event detection
2. Firebase middleware
3. Gemini AI integration
4. End-to-end threat protection
5. Hardening and hackathon preparation

## Development

To build this Android project:

```bash
./gradlew assembleDebug
```

To run tests:

```bash
./gradlew test
```
