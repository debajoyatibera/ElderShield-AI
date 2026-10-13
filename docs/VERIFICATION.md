# Verification And Readiness

**Review date:** 2026-10-01
**Baseline:** `a715fc1` (`feat: initial commit — Phase 3B complete`) with existing local changes preserved.
**Assessment:** Local-development prototype; not production-ready.

## Evidence Matrix

| Feature | Implementation | Verification | Evidence | Status |
|---|---|---|---|---|
| English, Bengali, Hindi | Android string resources, persisted language preference, language passed to the callable | Android unit tests; Functions response tests for Bengali and Hindi | `app/src/main/res/values*/strings.xml`; `LanguagePreferencesTest`; `analyzeThreat.test.ts` | 🟡 Unit-level evidence; full UI/language round trip not tested |
| Message analysis | User submits message text through the shared analysis service | Mocked Gemini tests; Android unit tests; callable path compiled | `CheckMessageScreen.kt`; `ThreatAnalysisViewModelTest.kt` | 🟡 No live model or Android-to-emulator round trip |
| Link analysis | User submits link text; prompt explicitly forbids claims of URL reputation or destination checks | Prompt test and mocked model response | `functions/src/index.ts`; `analyzeThreat.test.ts` | 🟡 No live URL service or Gemini validation |
| Call analysis | User submits a call narrative; phone number alone returns UNKNOWN | Unit test and Functions emulator request | `CheckCallScreen.kt`; emulator returned UNKNOWN for `1234567890` | ✅ No-evidence behavior verified; no call detection or caller identity lookup |
| Firebase callable | Functions v2 `analyzeThreat` endpoint with Zod validation | Functions emulator accepted a valid call-only payload and rejected unsupported language | HTTP 200 UNKNOWN and HTTP 400 INVALID_ARGUMENT from local emulator | ✅ Callable contract branches verified locally |
| Gemini integration | Server-side Google GenAI SDK call, Gemini 2.5 Flash, `GEMINI_API_KEY` secret binding | Unit suite mocks the SDK; timeout cancellation tested using a fake clock | `functions/src/index.ts`; 35 Vitest cases | 🟡 Code and fallback tested; no live Gemini request |
| Safety validation | Request enums, nonblank content, 1,000-character limit, response enums/category/confidence validation, untrusted-content framing | Deterministic invalid-input and malformed-output tests | `functions/src/validators/schema.ts`; `analyzeThreat.test.ts` | ✅ Schema and fallback behavior tested; model accuracy is not proven |
| UNKNOWN fallback | Missing call evidence, invalid model output, timeout, API failure, and empty model response return UNKNOWN | Unit suite and local emulator call-only request | `functions/src/index.ts`; `analyzeThreat.test.ts` | ✅ Tested |
| Android unit tests | JUnit tests for domain, services, preferences, and view model | `ANDROID_HOME=/opt/android-sdk ANDROID_SDK_ROOT=/opt/android-sdk ./gradlew test` | BUILD SUCCESSFUL | ✅ Verified |
| Android lint and APK | Debug APK target; lint report generated | `ANDROID_HOME=/opt/android-sdk ANDROID_SDK_ROOT=/opt/android-sdk ./gradlew lint assembleDebug` | BUILD SUCCESSFUL; APK at `app/build/outputs/apk/debug/app-debug.apk`; 0 errors, 26 warnings | ✅ Build verified; warnings remain |
| Functions tests/build | Vitest and TypeScript compiler | `npm --prefix functions test`; `npm --prefix functions run build` | 35 tests passed; `tsc` passed | ✅ Verified |
| Functions emulator | Functions emulator only, demo project ID | Eleven callable HTTP requests across content types/languages and validation cases; emulator-only labeled mock, no Gemini credential or production project used | A–H returned HTTP 200 UNKNOWN with the matching language; I returned UNKNOWN; J/K returned INVALID_ARGUMENT | ✅ Codespace callable matrix verified; Firebase auth/App Check were MISSING in emulator logs |
| Android UI/emulator | Compose app build exists | No Android emulator binary or connected `adb` device in this environment | `adb devices -l` returned no devices; SDK has platform/build-tools but no emulator package | ⚠️ Not exercised |
| Production deployment | `.firebaserc` contains a default project alias; Android production Firebase configuration is absent | No deployment attempted; no `google-services.json`; emulator-only client configuration | `firebase.json`, `.firebaserc`, `MainActivity.kt` | 🔴 Not configured or deployed |

## Verification Environment

- JDK 17 and Gradle 8.4; installed Android SDK is `/opt/android-sdk`, with platform 34 and build-tools 34.0.0.
- `gradlew` originally lacked its Unix executable bit; it has been corrected. Standard `./gradlew` commands now work.
- Android unit tests pass. `lint assembleDebug` succeeds but reports 26 warnings: dynamic locale changes/App Bundle locale splitting, dependency update suggestions, unused string resources, ellipsis typography, and missing application icon.
- Functions declares Node.js 20. The Codespace host is Node.js 24.21.0. `nvm exec 20` reported that v20 is not installed; no Node runtime was installed for this task. Firebase CLI therefore used Node 24 and warned about the mismatch.
- Firebase Functions emulator verification used `demo-eldershield`, not the `.firebaserc` default project. Emulator logs could not access Secret Manager because the environment is not authenticated. The tested insufficient-evidence/invalid-input branches do not read the secret.
- No Android device/emulator, Gemini live call, production deployment, or full Android-to-Functions flow was verified.

## Security Review

| Control | Current state |
|---|---|
| Authentication | Not enforced; handler does not check `request.auth`. Emulator logged `auth=MISSING`. |
| Firebase App Check | Not enforced; callable does not set `enforceAppCheck`; emulator logged `app=MISSING`. |
| Rate limiting | No per-user, per-IP, or persistent rate limiting found. |
| Abuse/cost controls | Request is capped at 1,000 characters; callable timeout is 15 seconds; model output is capped at 300 tokens; Gemini call is aborted at 10 seconds. No rate quota or budget configuration is present in this repository. |
| Input validation | Zod requires MESSAGE/LINK/CALL, en/bn/hi, trimmed nonblank text, and at most 1,000 characters. Invalid data is rejected before Gemini. |
| Logging/privacy | Function source contains no application logging of submitted content. Submitted text is transmitted to Firebase and then Gemini for analysis. The app does not persist submitted analyses in the reviewed flow; DataStore persists the selected language. |
| API key handling | Backend uses `defineSecret("GEMINI_API_KEY")`; no real key or service-account file was found. Emulator Gemini calls need an ignored `functions/.secret.local` override or an authenticated Secret Manager setup. |
| Error leakage | Invalid requests return a generic INVALID_ARGUMENT error; Gemini/provider/parse failures map to generic UNKNOWN responses. |
| Timeout behavior | The Gemini SDK's supported `abortSignal` is wired to the 10-second controller; timed-out requests are cancelled and return UNKNOWN. |

**Public-endpoint warning:** The callable currently accepts requests without authentication or App Check and has no rate limiter. Do not expose it as a public production service until abuse controls, quotas, monitoring, and Firebase App Check policy are configured. App Check is not a substitute for authorization or rate limits.

## Production Checklist

No Firebase deployment was performed. Before deployment or release:

- Confirm the intended Firebase project, region, billing, quota, and budget alerts with the project owner. A `.firebaserc` alias is not evidence of deployment authorization.
- Set `GEMINI_API_KEY` in Secret Manager for that project; never put it in source, an APK, or a committed local file.
- Add production Android Firebase setup through the Google Services Gradle plugin and an ignored `google-services.json` provisioned for the intended app. Neither is configured here.
- Replace hardcoded demo Firebase options and `10.0.2.2:5001` with explicit emulator and production build variants. Verify release artifacts cannot target the demo emulator or silently fall back to the mock analyzer.
- Register Firebase App Check for Android, configure Play Integrity for production and a debug provider for local testing, then enforce App Check on the callable after verified client rollout.
- Choose the identity model and implement server-side rate limits/quotas, abuse monitoring, alerting, and cost controls. Test unauthenticated, invalid, and over-limit requests.
- Review privacy/consent copy, third-party Gemini data processing, retention, accessibility, release signing, and incident response. Test a release candidate against the intended Firebase project before distribution.

## Android Detection Roadmap

Automatic interception is deliberately not implemented. Current manifest declares no SMS, call-log, microphone, contacts, or notification permissions.

1. **Recommended next product increment: user-initiated sharing.** Add Android Sharesheet intake for text/plain so users explicitly choose a message or link and review it before submission. This avoids broad SMS permissions and keeps analysis in the foreground. It is not implemented or UI-tested yet.
2. **SMS detection:** Do not add background SMS reading by default. `READ_SMS`/`RECEIVE_SMS` and SMS receiver access are sensitive and restricted by Google Play policy, commonly requiring the app to qualify as the user's default SMS handler. Start with explicit copy/paste or Sharesheet. Any broader access needs a permission/policy review, clear consent, data minimization, and device/API-level tests.
3. **Link detection:** Analyze only user-shared text unless a separate, reviewed URL-reputation service is introduced. Do not imply that a link was opened, verified, or checked for malware. Any server-side URL fetching needs SSRF protections and a separate threat model.
4. **Call-risk assistance:** Keep the current manual narrative flow; do not record calls or inspect call logs. A future screening feature would require evaluating Android's `CallScreeningService`/user role flow, policy eligibility, explicit disclosure, and supported-device behavior. Caller number alone must remain insufficient evidence.
5. **Consent and privacy:** Before sending shared content, show what will be transmitted to Firebase/Gemini, obtain a deliberate user action, avoid storing raw content, and never include message/call content in logs or analytics. Provide a clear privacy notice and retention/deletion policy.
6. **Background work:** No background processing is needed for the current user-triggered workflow. If later introduced, use an explicit opt-in, minimize queued content, define expiration/deletion behavior, and test lifecycle/network behavior; do not silently collect communications.

## CODESPACES-ONLY VERIFICATION

**Development target:** GitHub Codespaces with VS Code Desktop as the remote client. No Windows-side Java, Gradle, Android SDK/emulator, Node, npm, or Firebase CLI is required. Build/test commands run in the Codespace terminal.

| Area | Codespace evidence | Status |
|---|---|---|
| Java / Gradle | JDK 17.0.20; Gradle wrapper 8.4; wrapper mode 755 | ✅ VERIFIED |
| Android SDK | `/opt/android-sdk`; platform 34; build-tools 33.0.1 and 34.0.0; SDK env vars must be set per command in this Codespace | ✅ VERIFIED |
| Node / npm / Firebase CLI | Node 24.21.0; npm 11.19.0; Firebase CLI 15.30.0. Functions declares Node 20, which is not installed in this Codespace | 🟡 PARTIALLY VERIFIED (runtime mismatch) |
| Android source | Kotlin compilation occurs during Gradle test/build; Android unit tests pass | ✅ VERIFIED |
| Android build | `lint assembleDebug` and debug APK generation pass; lint has 26 warnings | ✅ VERIFIED |
| Android device / AVD | `adb devices -l` empty; emulator binary and AVDs absent; no image download attempted | ⚠️ BLOCKED IN CURRENT CODESPACE ENVIRONMENT |
| Firebase emulator | Functions emulator ran on port 5001 with `demo-eldershield`; emulator-only mock handled A–H and validation cases I–K; reusable smoke script passes 11/11 | ✅ VERIFIED |
| Gemini | Unit suite mocks the GenAI SDK; emulator demo uses a separate guarded mock response and skips secret binding | 🟡 MOCKED GEMINI VERIFIED; LIVE GEMINI NOT VERIFIED |
| Android UI E2E | No device available; `10.0.2.2` routing and result-screen behavior not observed | ⚠️ BLOCKED; NO UI E2E CLAIM |

### Reproducible Codespace Commands

Run these from the repository root in the Codespace. The example file contains only a non-secret mock flag; copying it creates an ignored project-specific Firebase env file:

```bash
cp functions/demo-eldershield.env.example functions/.env.demo-eldershield
./gradlew test
./gradlew lint
./gradlew assembleDebug
npm --prefix functions test
npm --prefix functions run build
./node_modules/.bin/firebase emulators:start --project demo-eldershield --only functions
```

With this file present, only the Functions emulator returns a deterministic response labelled `DEMO MOCK`; it always reports `UNKNOWN` and says that no content assessment occurred. The handler requires both Firebase's `FUNCTIONS_EMULATOR=true` marker and `ELDERSHIELD_USE_MOCK_GEMINI=true`. Production execution ignores the mock flag and keeps the Gemini secret binding. No real Gemini key is needed for this demo mode.

The current Codespace needs the SDK path exported if it is not in the shell environment:

```bash
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
```

After the emulator prints that it is ready, run this in a second Codespace terminal. The script verifies the mock-only env file before sending any requests and covers A–K:

```bash
node functions/scripts/demo-emulator-smoke.cjs
```

It does not invoke Gemini. For one-off callable checks, these examples also do not invoke Gemini:

```bash
curl -sS http://127.0.0.1:5001/demo-eldershield/us-central1/analyzeThreat \
	-H 'Content-Type: application/json' \
	-d '{"data":{"contentType":"CALL","content":"CALL DESCRIPTION:   ","appLanguage":"en"}}'

curl -sS -i http://127.0.0.1:5001/demo-eldershield/us-central1/analyzeThreat \
	-H 'Content-Type: application/json' \
	-d '{"data":{}}'
```

Stop the Functions emulator with Ctrl+C when finished. No emulator images, large model files, or duplicate servers are needed. Firebase emulator test/build has no Gemini key requirement. Run provider-failure case L through the mocked SDK unit suite; emulator mock mode deliberately bypasses the provider.

### Android Validation Boundaries

- **SOURCE VERIFIED:** Kotlin source, manifest/resources, and backend contract are present; Gradle compilation is exercised by the Android tasks.
- **UNIT TEST VERIFIED:** Android JUnit suite and Functions Vitest suite pass.
- **BUILD VERIFIED:** Android lint task and debug APK assembly pass; resource/manifest processing is part of the Android build.
- **DEVICE/UI VERIFIED:** ⚠️ BLOCKED IN CURRENT CODESPACE ENVIRONMENT. No Android device or AVD is available; do not claim UI E2E.

## LOCAL END-TO-END DEMO STATUS

**Overall: 🟡 Partially verified. Full Android UI-to-Functions E2E is ⚠️ blocked by the missing Android emulator/device.** No UI screenshot or live-model result is available.

- **Environment:** JDK 17; Android SDK at `/opt/android-sdk` (platform 34, build-tools 34.0.0); `ANDROID_HOME`/`ANDROID_SDK_ROOT` unset by default but explicit values work. Node 24.21.0 and npm 11.19.0; Node 20 is not installed. Firebase CLI 15.30.0.
- **Device:** `adb devices -l` listed no devices. `/opt/android-sdk/emulator/emulator` and AVDs are absent. No system image was installed.
- **Firebase:** Functions emulator started on `127.0.0.1:5001` with `--project demo-eldershield --only functions`. The emulator loaded `us-central1-analyzeThreat`; it did not target the `.firebaserc` project. Curl/Node requests reached the callable. The Android source is configured for `10.0.2.2:5001`, but device routing was not tested.
- **Gemini:** Live Gemini is **NOT VERIFIED**. No key is required or used by the deterministic demo. A–H use the guarded emulator mock; L uses a mocked GenAI provider failure in Vitest. No live model request was made.
- **Android UI:** **NOT VERIFIED.** App launch, language switching/persistence after restart, loading/results, navigation, and backend error presentation were not observed on a device.
- **Emulator limitation:** Firebase CLI warned that Node 24 was used instead of the declared Node 20. Emulator mock mode omits Gemini secret binding; the tested A–K paths do not call Gemini.
- **Evidence:** `app/build/outputs/apk/debug/app-debug.apk` exists from the verified debug build. No screenshots or device logs exist for this milestone.

### Deterministic Test Matrix

Cases A–K are direct requests to the local Functions emulator in guarded mock mode, except case L, which is a unit-level mocked provider failure. A–H exercise the callable HTTP contract for message/link/call across en/bn/hi; the demo mock deliberately returns UNKNOWN for every content type. These are not Gemini classifications. The Android column records the actual verification boundary; no UI result was observed.

| Case | Synthetic input → backend request | Backend response observed | Android UI result |
|---|---|---|---|
| A | `DEMO ONLY: Example Bank requests a one-time demo code to release a test payment.` → `MESSAGE/en` | Emulator mock HTTP 200: `UNKNOWN`, `en`, explanation labels `DEMO MOCK` | Not run; no device. UI result unverified. |
| B | `শুধু ডেমো: Example Bank পরীক্ষামূলক পেমেন্টের জন্য এককালীন কোড চাইছে।` → `MESSAGE/bn` | Emulator mock HTTP 200: `UNKNOWN`, `bn`, localized mock label | Not run; no device. UI result unverified. |
| C | `केवल डेमो: Example Bank परीक्षण भुगतान जारी करने के लिए एक बार का कोड मांगता है।` → `MESSAGE/hi` | Emulator mock HTTP 200: `UNKNOWN`, `hi`, localized mock label | Not run; no device. UI result unverified. |
| D | `https://s.example.invalid/a1b2` → `LINK/en` | Emulator mock HTTP 200: `UNKNOWN`, `en`, explanation labels `DEMO MOCK` | Not run; no device. UI result unverified. |
| E | `https://s.example.invalid/bn-demo` → `LINK/bn` | Emulator mock HTTP 200: `UNKNOWN`, `bn`, localized mock label | Not run; no device. UI result unverified. |
| F | Synthetic caller claims to represent Example Bank and asks for a demo code → `CALL/en` | Emulator mock HTTP 200: `UNKNOWN`, `en`, explanation labels `DEMO MOCK` | Not run; no device. UI result unverified. |
| G | `ডেমো কলকারী Example Bank-এর প্রতিনিধি বলে পরিচয় দিয়ে একটি পরীক্ষামূলক কোড চাইছে।` → `CALL/bn` | Emulator mock HTTP 200: `UNKNOWN`, `bn`, localized mock label | Not run; no device. UI result unverified. |
| H | `डेमो कॉल करने वाला Example Bank का प्रतिनिधि बनकर परीक्षण कोड मांगता है।` → `CALL/hi` | Emulator mock HTTP 200: `UNKNOWN`, `hi`, localized mock label | Not run; no device. UI result unverified. |
| I | `CALL DESCRIPTION:   ` → `CALL/en` | Emulator HTTP 200: `UNKNOWN` | Not run; no device. Expected UNKNOWN screen is unverified. |
| J | Synthetic message → `MESSAGE/fr` | Emulator HTTP 400: `INVALID_ARGUMENT` | Not run; client-side handling of this error is unverified. |
| K | Empty callable data → `{}` | Emulator HTTP 400: `INVALID_ARGUMENT` | Not run; client-side handling of this error is unverified. |
| L | `DEMO ONLY: simulated Gemini failure.` → `MESSAGE/en` | Mocked provider rejection: generic `UNKNOWN` fallback | Not run; no device. Expected UNKNOWN screen is unverified. |

All matrix data is synthetic. The `.invalid` links are reserved examples and must not be opened as real destinations. The emulator mock does not classify content; UNKNOWN is intentional and prominently labeled in the explanation/action text.

### Synthetic Demo Examples

- Bank/payment: `DEMO ONLY: Example Bank requests a one-time demo code to release a test payment.`
- Fake reward: `DEMO ONLY: You won 10,000 Example Credits. Claim at https://rewards.example.invalid/lottery.`
- Short-link-shaped URL (not a real shortening service): `https://s.example.invalid/a1b2`
- Call narrative: `CALL DESCRIPTION: A synthetic caller claims to represent Example Bank and asks for a demo code.`
- Benign: `DEMO ONLY: Dinner has moved to 6:00. No link, payment, or code request.`
- Insufficient evidence: `CALL DESCRIPTION:   ` (no description after the marker).

These examples are not guaranteed classifications. A–H use the emulator's deterministic non-analysis mock; L is a unit-level provider failure fixture. No Gemini classification was requested.

### 3-MINUTE HACKATHON DEMO SCRIPT

**Proposed script only. The UI flow has not been executed on a device. The currently verified Codespace demo is API-only and returns a labeled UNKNOWN mock, not a scam classification. Use the UI steps only after a local Android device/emulator becomes available.**

- **00:00 — Problem:** Explain that suspicious messages and calls pressure people into sharing money or codes; results are guidance, not identity or URL verification.
- **00:20 — Open ElderShield AI:** Show the implemented home screen and explain that checks are user-initiated.
- **00:40 — Suspicious message:** Submit the synthetic Example Bank demo message and show the result only if the chosen backend is actually connected.
- **01:10 — Analysis boundary:** Explain what evidence the result uses and what it cannot verify.
- **01:30 — Bengali mode:** Switch language and show the translated interface only after confirming it works on device.
- **02:00 — Link check:** Submit the short-link-shaped `.invalid` example; explicitly state it is text-only analysis, not a visited or reputation-checked URL.
- **02:30 — Call-risk narrative:** Submit the synthetic narrative; do not imply caller ID or telecom verification.
- **02:50 — UNKNOWN behavior:** Submit the empty-description case and explain why insufficient evidence does not become SAFE.
- **03:00 — Impact:** Close with user control, multilingual access, and the remaining work before public use.

## Exact Commands

```bash
npm --prefix functions test
npm --prefix functions run build
ANDROID_HOME=/opt/android-sdk ANDROID_SDK_ROOT=/opt/android-sdk ./gradlew test
ANDROID_HOME=/opt/android-sdk ANDROID_SDK_ROOT=/opt/android-sdk ./gradlew lint assembleDebug
```

Use the Codespace Android SDK path `/opt/android-sdk`; no local Windows SDK is required. Live Gemini and production checks require authorized project configuration and were not run.
