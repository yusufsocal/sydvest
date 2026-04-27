# Codebase Issue Audit
_Generated: 2026-04-27_

---

## High Priority

### 1. Location callback never unregistered — memory + battery leak
**File:** `app/src/main/java/.../ui/map/MapUtils.kt`  
`startLocationUpdates()` calls `fusedClient.requestLocationUpdates()` but `removeLocationUpdates()` is never called anywhere in the codebase. The callback runs forever, even after the screen is destroyed.

### 2. Infinite `while(true)` loop in `LaunchedEffect(Unit)` with no cleanup
**File:** `app/src/main/java/.../ui/map/MapScreen.kt` — lines 100–105  
Polls location services every 1 000 ms indefinitely. `LaunchedEffect(Unit)` is never cancelled, so this keeps running for the app's lifetime.

### 3. `HttpClient` created but never closed
**Files:** `app/src/main/java/.../data/warningdata/AlertDataSource.kt` lines 17–23, `app/src/main/java/.../data/weatherdata/WMSDataSource.kt` line 14  
`HttpClient(CIO)` instances are class-level fields that are never closed. This leaks network connections and thread pool resources.

### 4. JSON array access without bounds checking
**File:** `app/src/main/java/.../ui/map/MapUtils.kt` — lines 118–120  
`coords[0].jsonArray` and `pair[1].jsonPrimitive.double` access array indices directly. Malformed GeoJSON from the API will throw `IndexOutOfBoundsException` and crash the app.

### 5. Null-unsafe JSON access in `drawAlerts`
**File:** `app/src/main/java/.../ui/map/MapUtils.kt` — lines 139–144  
`features.geometry?.coordinates?.jsonArray` can be null, but is then cast and indexed without null checks. API data variations will cause a crash.

### 6. Loading state never cleared on error
**File:** `app/src/main/java/.../ui/map/MapViewModel.kt` — lines 84–85  
If `locationRepo.getArea()` or `alertRepo.getAlertList()` fails, `isLoading` remains `true` permanently. Users see a spinner forever on network failure.

---

## Medium Priority

### 7. Repositories recreated on every recomposition
**File:** `app/src/main/java/.../ui/AppNavHost.kt` — lines 35–43  
`WMSDataSourceImpl`, `LocationRepository`, `AlertDataSourceImpl`, and `SearchDataSourceImpl` are instantiated inside a composable. Every recomposition creates new instances, making `LocationRepository`'s cache useless and multiplying the `HttpClient` leaks.

### 8. Blocking Java I/O on network call
**File:** `app/src/main/java/.../data/searchdata/SearchDataSource.kt` — line 22  
`java.net.URL(url).readText()` is a synchronous Java API call. Although it's inside `withContext(Dispatchers.IO)`, it's inconsistent with the Ktor usage elsewhere and harder to cancel.

### 9. Silent error swallowing in search
**File:** `app/src/main/java/.../data/searchdata/SearchDataSource.kt` — lines 18–44  
The catch block returns an empty list with no logging, making it impossible to distinguish a network error from a genuine zero-result response.

### 10. `println()` / `printStackTrace()` instead of `Log`
**Files:** `app/src/main/java/.../data/warningdata/AlertDataSource.kt` lines 29, 32 — `app/src/main/java/.../data/weatherdata/WMSDataSource.kt` line 34  
These don't appear under the app's Logcat tag, making production debugging difficult.

### 11. Race condition between permission check and map ready
**File:** `app/src/main/java/.../ui/map/MapScreen.kt` — lines 70–98  
`centerMapOnUserLocation` is called on line 80 and `startLocationUpdates` in a separate `LaunchedEffect`. `mapViewRef` may still be null when the second effect fires; order of effects is not guaranteed.

### 12. `AndroidView` update block runs on every recomposition
**File:** `app/src/main/java/.../ui/map/components/MapOsmView.kt` — lines 69–98  
`updateWmsLayer()` and `drawAlerts()` are called in every update, removing and re-adding overlays unnecessarily. This can cause visual jank and repeated work if state changes rapidly.

### 13. `mutableMapOf` cache accessed from multiple coroutines
**File:** `app/src/main/java/.../data/repository/LocationRepository.kt` — line 10  
The cache is a plain (non-thread-safe) `MutableMap` accessed concurrently from coroutines without synchronization.

### 14. `newArea` parameter should be `val`
**File:** `app/src/main/java/.../ui/map/MapViewModel.kt` — line 70  
`private var newArea: AreaData` is never mutated after assignment; should be `val` for correctness and clarity.

---

## Low Priority

### 15. Missing `@SuppressLint("MissingPermission")` on `startLocationUpdates`
**File:** `app/src/main/java/.../ui/map/MapUtils.kt` — line 168  
The annotation is present on `centerMapOnUserLocation` but missing on `startLocationUpdates`, causing inconsistent lint warnings.

### 16. URL parameters not encoded
**File:** `app/src/main/java/.../ui/map/MapUtils.kt` — lines 55–85  
The `TIME` parameter is concatenated raw into the URL string. If `selectedTime` ever contains special characters, the request will be malformed.

### 17. Hardcoded API base URLs
**Files:** Multiple data sources  
Base URLs are scattered across classes rather than centralized in `BuildConfig` or a constants file, making environment switching cumbersome.

---

## Summary

| Priority | Count |
|----------|-------|
| High     | 6     |
| Medium   | 8     |
| Low      | 3     |
| **Total**| **17**|

**Recommended fix order:** Issues 1–3 (location leak, infinite loop, HttpClient leak) affect every user session. Issue 6 (loading state never cleared) silently breaks the entire UI on any network error. Address these before the others.
