# Refactoring Analysis — Issues Found During Test Creation

## Context
After writing 181 tests across the entire codebase, these are the structural issues observed that could cause real bugs — not style nits or theoretical concerns. Ordered by severity.

---

## 1. CRITICAL — `DetailsViewModel.allLists` is `lateinit` loaded async

**File:** `composeApp/src/commonMain/kotlin/features/details/ui/DetailsViewModel.kt:81-89`

```kotlin
private lateinit var allLists: List<ListItem>  // line 81

init {
    viewModelScope.launch(Dispatchers.IO) {    // async on IO
        allLists = detailsInteractor.getAllLists()
    }
    fetchPersonalRating()   // runs immediately
    initFetchDetails()      // runs immediately
}

fun getAllLists(): List<ListItem> {
    return allLists  // CRASH if called before IO completes
}
```

**The bug:** `getAllLists()` is called from the UI when the user opens the list overlay. If the IO task hasn't completed yet, it throws `UninitializedPropertyAccessException` — a crash. The window is small (DB query is fast), but it exists on slow devices or large databases.

**Fix:** Replace `lateinit var` with `MutableStateFlow<List<ListItem>>(emptyList())`. The UI already consumes it reactively, so this is a drop-in change.

---

## 2. CRITICAL — `moveItemToList()` has no transaction safety

**File:** `composeApp/src/commonMain/kotlin/database/repository/DatabaseRepositoryImpl.kt:62-83`

```kotlin
override suspend fun moveItemToList(...): ContentEntity? {
    deleteItem(contentId, mediaType, newListId)     // Step 1: cleanup duplicate
    insertItem(contentId, mediaType, newListId)     // Step 2: insert into new list
    return deleteItem(contentId, mediaType, currentListId)  // Step 3: remove from old list
}
```

**The bug:** Three separate DAO calls with no `@Transaction`. If the app is killed or crashes between step 2 (insert) and step 3 (delete from old list), the item exists in **both** lists. Room supports `@Transaction` on repository methods — this is a one-line annotation fix.

**Fix:** Add `@Transaction` to the `moveItemToList` method in the DAO layer, or wrap the three calls in `appDatabase.runInTransaction { }`.

---

## 3. HIGH — `buildUrl()` crashes on malformed query parameters

**File:** `composeApp/src/commonMain/kotlin/network/util/NetworkExtensions.kt:63-65`

```kotlin
urlSplit[1].split("&").forEach {
    val keyValue = it.split("=")
    existingMap[keyValue[0]] = keyValue[1]  // IndexOutOfBoundsException if "key=" or "key"
}
```

**The bug:** If a URL parameter has no value (e.g., `?key=` or `?key`), `keyValue[1]` throws `IndexOutOfBoundsException`. Currently the app only passes well-formed URLs from the TMDB API, but any future change that introduces a parameter without a value will crash at runtime with no obvious cause.

**Fix:** Use `getOrElse`: `existingMap[keyValue[0]] = keyValue.getOrElse(1) { "" }`. Or better yet, use Ktor's built-in `Url` parser instead of manual string splitting.

---

## 4. MEDIUM — Mixed dispatcher patterns make code harder to reason about

**Files:** `HomeViewModel.kt:56-78`, `WatchlistViewModel.kt:54,90,118,138,181,202,228`

Two related patterns:

**Pattern A — HomeViewModel:** `loadHomeScreen()` runs on Main (default), but calls `loadWatchlist()` which launches a **separate** coroutine on `Dispatchers.IO`. This means `_loadState` is set to `Success` (line 70) before `_myWatchlist` is populated. It works — the watchlist carousel just appears later — but it caused two test failures during test creation because the timing is non-obvious.

**Pattern B — WatchlistViewModel:** Nearly every method launches on `Dispatchers.IO`, but `loadAllLists()` switches back to `Dispatchers.Main` mid-function (line 202) because it writes to Compose `MutableState`. This IO→Main nesting works but is fragile — forgetting the `withContext(Main)` on a future change would cause a Compose state write from the wrong thread.

**Recommendation:** The consistent pattern should be: ViewModels run on Main (via `viewModelScope`), interactors use `withContext(Dispatchers.IO)` for blocking work. This way the dispatcher decision lives in one place.

---

## 5. MEDIUM — `WatchlistInteractor` undo state is not thread-safe

**File:** `composeApp/src/commonMain/kotlin/features/watchlist/domain/WatchlistInteractor.kt:26-27, 93-131`

```kotlin
private var lastRemovedItem: ContentEntity? = null
private var lastMovedListId: Int? = null
```

These mutable fields are read/written from coroutines on `Dispatchers.IO`. In practice, user actions are sequential (remove → undo), so concurrent access is unlikely. But the WatchlistViewModel launches each operation in a **new** `viewModelScope.launch(Dispatchers.IO)` coroutine, so rapid taps could theoretically race.

**Not urgent** — the real-world risk is low since the UI prevents rapid fire (snackbar flow). But if this pattern is ever reused, it should use `Mutex` or `AtomicReference`.

---

## 6. LOW — `println()` error handling throughout interactors

**Files:** `DetailsInteractor.kt` (8 locations), `HomeInteractor.kt` (3 locations), `WatchlistInteractor.kt` (1 location)

Every API error is handled with:
```kotlin
is Right -> {
    println("getXxx failed with error: ${response.error}")
}
```

This goes to stdout (invisible in production). The error is either silently swallowed (returns null/empty list) or set on a state object. The user sees "failed" state but has no diagnostic info.

**Not urgent** — the app handles errors gracefully at the UI level. But when debugging production issues, `println()` provides zero observability. A proper logging framework (or even just Android's `Log.e()`) would help.

---

## Summary

| # | Issue | Severity | Effort | Fix |
|---|-------|----------|--------|-----|
| 1 | `lateinit allLists` in DetailsViewModel | **CRITICAL** | 5 min | Replace with `MutableStateFlow(emptyList())` |
| 2 | `moveItemToList` non-atomic | **CRITICAL** | 5 min | Add `@Transaction` |
| 3 | `buildUrl` IndexOutOfBounds | **HIGH** | 5 min | Use `getOrElse(1) { "" }` |
| 4 | Mixed dispatcher patterns | **MEDIUM** | 1-2 hrs | Move IO dispatching to interactors |
| 5 | WatchlistInteractor undo state | **MEDIUM** | 15 min | Add `Mutex` or use `AtomicReference` |
| 6 | `println()` error handling | **LOW** | 30 min | Replace with structured logging |

**Recommendation:** Fix #1, #2, and #3 immediately — they're all 5-minute fixes that prevent real crashes or data corruption. Items #4-#6 are worth tracking but not urgent.

---

## 7. ~~DONE~~ — `isDefault` field wired up (localization fix)

**Context:** DB v7 added `isDefault: Boolean` to `ListEntity` to replace the pattern of identifying default lists by hardcoded IDs (`DefaultLists.WATCHLIST.listId = 1`, `DefaultLists.WATCHED.listId = 2`) or by name (which caused localization complexity).

**What's done:** The column exists in the schema, migrations mark existing lists correctly, and `onCreate` inserts default lists with `isDefault = 1`.

**What's missing:**
- `ListEntityDao` has no query for `isDefault` (e.g., `getDefaultLists(): List<ListEntity>`)
- `DatabaseRepository` doesn't expose default list lookups via flag
- App logic still uses `DefaultLists.listId` hardcoded integers to identify defaults

**Recommended fix:** Add a `getDefaultLists()` DAO query, expose it through `DatabaseRepository`, then replace hardcoded `DefaultLists.listId` references in `WatchlistViewModel`, `DetailsViewModel`, and `DatabaseRepositoryImpl` with a flag-based lookup. The `DefaultLists` enum can then be scoped to UI labels only, removing its role as a source of truth for DB identity.
