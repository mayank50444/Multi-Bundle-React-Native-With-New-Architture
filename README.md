## Building Multi‑Bundle React Native on the New Architecture (RN 0.77) — A Practical Guide

If your React Native app is getting large or your teams ship features independently, a single mega‑bundle can slow you down. In this POC, we split the app into a shared “common” bundle and multiple feature (“business”) bundles, then dynamically load those bundles at runtime — all on the New Architecture (Bridgeless) with Hermes. This article walks through the why, the how, and the gotchas, with code you can adopt today.

### TL;DR

- We boot the app with a small, shared `common` bundle.
- Feature bundles (e.g., Biz1, Biz2) are compiled separately and loaded on demand.
- Metro is customized to keep module IDs stable and non‑overlapping across bundles.
- On Android (Bridgeless), we start one Hermes runtime and mount React roots from any feature bundle dynamically.

### Why go multi‑bundle?

- Modularity and faster builds: ship only what changed.
- On‑demand features: mount screens when users actually need them.
- Safer experiments/tenants: isolate features behind separate artifacts.
- Team autonomy: separate pipelines per feature group.

## Architecture at a glance

1) Common bundle — built from `index.common.js`. It holds shared primitives, base UI, and cross‑cutting code. The Android host boots with this bundle.

2) Business bundles — one entry file per feature (e.g., `index.biz1.js`, `index.biz2.js`), each registering a unique component key via `AppRegistry.registerComponent(...)`.

3) Stable module IDs — a custom Metro serializer assigns IDs from disjoint ranges and excludes modules already present in the common bundle. No duplication, no collisions.

4) Android host (Bridgeless) — we start `ReactHost` once and load additional bundles via `JSBundleLoader.createAssetLoader(...)`, then mount individual React roots inside Jetpack Compose.

## What’s in this repo

- Common entry: `index.common.js`
- Features: `index.biz1.js`, `index.biz2.js`
- Metro configs: `metro.common.config.js`, `metro.business.config.js`
- Serializer helpers: `bundle.js` (module ID ranges, common map persistence)
- Build script: `build.sh` (emits `common.android.bundle`, `biz1.android.bundle`, `biz2.android.bundle`, etc.)
- Android Bridgeless glue:
  - `MainApplication.kt` (boots with common bundle, enables Bridgeless)
  - `MultipleReactActivityDelegate.kt` (loads a target bundle and mounts a React root)
  - `ReactBizScreen.kt` + Compose navigation (choose Biz1/Biz2 and embed the React view)

## The Metro trick that makes this work

We extend Metro’s serializer to keep module IDs deterministic and non‑overlapping:

- Each entry file starts in a different numeric range (e.g., common vs biz ranges).
- When building the common bundle, we write out a `path:id` map to disk.
- When building a business bundle, we load that map and skip anything already included in common. Any new modules get IDs from the business range.

This ensures that business bundles can rely on code in common without redefining it, and their own modules won’t collide with other bundles.

## Android: loading feature bundles at runtime (Bridgeless)

On RN 0.77 with Hermes and Bridgeless enabled:

1) App boots with `common.android.bundle`.
2) A user taps a button in the native screen (Jetpack Compose).
3) We create a delegate that loads the target feature bundle (e.g., `assets://biz1.android.bundle`) and then call `loadApp('Biz1Bundle')`.
4) Compose embeds the resulting `ReactRootView` in place.

The runtime isn’t restarted; you just mount a new React root whose code lives in a separate JS asset.

## Build and run

Build all Android bundles:

```bash
./build.sh
```

Run the app:

```bash
npm run android
```

You’ll land on a native Compose screen with buttons for Biz1/Biz2. Tap to mount each feature from its own JS bundle.

## Add your own feature bundle

1) Create `index.biz3.js` and register a unique key:

```js
import {AppRegistry} from 'react-native';
AppRegistry.registerComponent('Biz3Bundle', () => Biz3App);
```

2) Extend your build to emit `biz3.android.bundle` using `metro.business.config.js`.

3) Map the key to an asset path on Android (e.g., in your screen logic):

```js
// pseudo-switch
// 'Biz3Bundle' -> 'assets://biz3.android.bundle'
```

4) Add a button or route to launch Biz3.

## Caveats you should know about

- State isn’t shared across React roots by default. If needed, share via native or a singleton in the common bundle.
- All bundles must agree on the same RN version and ABI.
- Keep Hermes enabled across bundles for consistent behavior.
- Business bundles filter polyfills and `__prelude__` to avoid duplication.
- iOS parity: this repo focuses on Android Bridgeless. iOS can use a similar pattern (loading additional jsbundle files) and can be wired next.

## Where to take this next

- CI jobs per feature bundle with semantic versions.
- Remote update service (CDN, signatures, rollback).
- Source maps per bundle for crash symbolication.
- iOS implementation mirroring the Android flow.

If you want this turned into a production template (scripts, CI, OTA, integrity checks, and iOS parity), I can scaffold the remaining pieces.


