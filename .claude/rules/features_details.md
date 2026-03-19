# Features Details

## Home

**Route:** `home`
**Purpose:** Entry point. Shows curated trending content, the user's watchlist, trending people, and upcoming movies.

### Structure
- `HomeScreen.kt` — Route definition
- `HomeEvent.kt` — `LoadHome`, `ReloadWatchlist`, `OnError`
- `HomeInteractor.kt` — Network + DB fetching
- `HomeViewModel.kt` — Orchestrates loading
- `HomeView.kt` — Main composable
- `HomeState.kt` — Extends `LoadStatusState`, holds `trendingList`
- `carousel/` — `TrendingContainer`, `WatchlistContainer`, `ComingSoonContainer`
- `featured/` — `FeaturedContainer`, `SecondaryFeaturedContainer`, `PersonFeaturedContainer`

### State (StateFlows)
| Field | Type | Description |
|---|---|---|
| `loadState` | `DataLoadStatus` | Loading / Success / Failed |
| `trendingMulti` | `List<GenericContent>` | Trending movies + shows |
| `myWatchlist` | `List<GenericContent>` | User's saved watchlist items |
| `trendingPerson` | `List<PersonDetails>` | Trending people |
| `moviesComingSoon` | `List<GenericContent>` | Upcoming movies |

### Data Flow
1. `init` → `onEvent(LoadHome)` → `loadHomeScreen()`
2. All 4 sections load in parallel via coroutines
3. `getAllWatchlist()` fetches `ContentEntity` from DB then fetches details per item from Movie/ShowRepository
4. `getMoviesComingSoon()` uses `DateUtils.getComingSoonDates()` for date range filtering
5. `loadState` set to `Success` when all complete, `Failed` if any throws

### Special Behaviors
- **Featured item** = first item in `trendingMulti`
- **Secondary featured** = first item of the opposite media type (MOVIE↔SHOW)
- **Trending people** filtered: must have `knownForDepartment` and ≥ 3 `knownFor` entries
- **iOS-only:** Background image hides after `HOME_BACKGROUND_HIDE_OFFSET` scroll threshold

---

## Browse

**Route:** `browse`
**Purpose:** Paginated grid explorer for movies and shows with sort options and media type tabs.

### Structure
- `BrowseScreen.kt` — Route definition
- `BrowseEvent.kt` — `UpdateSortType(sortTypeItem, mediaType)`, `UpdateMediaType(mediaType)`, `OnError`
- `BrowseInteractor.kt` — Returns `Flow<PagingData<GenericContent>>` via `MediaContentPagingSource`
- `BrowseViewModel.kt` — Manages pagers and sort state
- `BrowseView.kt` — `HorizontalPager` + `LazyVerticalGrid` + collapsing tab row
- `CollapsingTabRow.kt` — Animated yellow tab indicator, Movies/Shows tabs
- `BrowseSortBottomSheet.kt` — Sort options per media type
- `MediaContentPagingSource.kt` — Custom `PagingSource` (page size 20)

### State
| Field | Type | Description |
|---|---|---|
| `moviePager` | `PagingData<GenericContent>` | Paged movies |
| `showPager` | `PagingData<GenericContent>` | Paged shows |
| `movieSortTypeSelected` | `ContentListType?` | Current movie sort |
| `showSortTypeSelected` | `ContentListType?` | Current show sort |
| `_mediaTypeSelected` | `MediaType` | Current tab (MOVIE / SHOW) |

### Sort Options
- **Movies:** NowPlaying, Popular, TopRated, Upcoming
- **Shows:** AiringToday, Popular, TopRated, OnTheAir

### Special Behaviors
- Sort state is stored in **MainViewModel** (`movieSortType`, `showSortType`) so it persists across navigation
- `cachedIn(viewModelScope)` prevents re-fetch on tab switch
- Responsive grid: `calculateCardsPerRow()` adjusts columns based on screen width
- Collapsing tab row uses `NestedScrollConnection` for coordinated scroll behavior

---

## Watchlist

**Route:** `watchlist`
**Purpose:** User-managed list system. Supports multiple lists (Watchlist, Watched, custom), sorting, moving items between lists, and undo on removal.

### Structure
- `WatchlistScreen.kt` — Route definition
- `WatchlistEvent.kt` — 9 events (see below)
- `WatchlistInteractor.kt` — DB CRUD + content detail fetching
- `WatchlistViewModel.kt` — Complex state with list map and undo logic
- `WatchlistView.kt` — Tab row, lazy list, snackbar
- `WatchlistState.kt` — Holds `listItems: MutableState<List<GenericContent>>`
- `WatchlistSnackbarState.kt` — `listId: Int?`, `itemAction: WatchlistItemAction`
- `DefaultLists.kt` — Enum: `WATCHLIST(id=1)`, `WATCHED(id=2)`, `ADD_NEW(id=999)`
- Components: `WatchlistCards`, `DeleteListDialog`, `ListRemovePopupMenu`, `CreateListBottomSheet`, `WatchlistTabItem`

### State
| Field | Type | Description |
|---|---|---|
| `loadState` | `DataLoadStatus` | Empty / Loading / Success / Failed |
| `allLists` | `List<WatchlistTabItem>` | All tabs including AddNew |
| `listContent` | `Map<Int, List<GenericContent>>` | listId → items |
| `selectedList` | `Int` | Current list ID |
| `sortType` | `WatchlistSort` | Media type filter + rating sort |
| `snackbarState` | `WatchlistSnackbarState` | Undo feedback |
| `selectedTabIndex` | `Int` | Current tab index |

### Events
| Event | Action |
|---|---|
| `LoadWatchlistData` | Load all list contents from DB |
| `RemoveItem(contentId, mediaType)` | Remove from current list, trigger snackbar |
| `SelectList(tabItem)` | Switch active tab |
| `UpdateSortType(watchlistSort)` | Apply new sort/filter |
| `UpdateItemListId(contentId, mediaType, listId)` | Move item to another list |
| `UndoItemAction` | Undo last remove or move |
| `LoadAllLists` | Reload tab list (used after create/delete) |
| `DeleteList(listId)` | Delete custom list |
| `OnSnackbarDismiss` | Hide snackbar |

### Special Behaviors
- **Undo system:** `lastRemovedItem` and `lastMovedListId` tracked in interactor for undo
- **Default lists** (WATCHLIST, WATCHED) cannot be deleted — enforced via long-press check
- **Custom lists** capped at `MAX_WATCHLIST_LIST_NUMBER` (10)
- **AddNew tab** always appears last if below the cap — tapping it opens `CreateListBottomSheet`
- **Sorting:** `WatchlistSort` holds `mediaType` filter and `WatchlistRatingSort` (PublicRating, PersonalRating, or null)
- **Cross-feature:** `MainViewModel.refreshLists` and `watchlistSort` are observed to sync state after Details screen actions

---

## Search

**Route:** `search`
**Purpose:** Debounced multi-type content search with paginated results and filter tabs.

### Structure
- `SearchScreen.kt` — Route definition
- `SearchEvent.kt` — `SearchQuery(query)`, `ClearSearchBar`, `FilterTypeSelected(filter)`, `OnError`
- `SearchInteractor.kt` — Returns `Flow<PagingData<GenericContent>>` via `SearchPagingSource`
- `SearchViewModel.kt` — Debounce logic, filter state, paged results
- `SearchView.kt` — Search bar + filter row + grid
- `SearchBar.kt` — TextField with leading search icon and trailing clear button
- `SearchFilters.kt` — `ScrollableTabRow` with 4 filter buttons
- `SearchTypeFilterItem.kt` — Enum: `TopResults`, `Movies`, `Shows`, `Person`
- `SearchPagingSource.kt` — Custom `PagingSource` supporting all 4 types

### State
| Field | Type | Description |
|---|---|---|
| `searchQuery` | `String` | Current input text |
| `searchResults` | `PagingData<GenericContent>` | Paged results |
| `searchFilterSelected` | `SearchTypeFilterItem` | Active filter tab |
| `searchDebounceJob` | `Job?` | Active debounce coroutine |

### Filter Types → API Endpoint
| Filter | MediaType | Endpoint |
|---|---|---|
| TopResults | null | `searchMultiByQuery` |
| Movies | MOVIE | `searchMovieByQuery` |
| Shows | SHOW | `searchShowByQuery` |
| Person | PERSON | `searchPersonByQuery` |

### Special Behaviors
- **Debounce:** 300ms (`SEARCH_DEBOUNCE_TIME_MS`) — previous job cancelled on each new keystroke
- **Keyboard:** Dismissed when user scrolls/taps the results grid
- `cachedIn(viewModelScope)` prevents re-fetch on recomposition

---

## Details

**Route:** `details/{contentId}?mediaType={mediaType}`
**Purpose:** Full content detail view for movies, shows, and people. Includes cast, videos, recommendations, streaming providers, watchlist management, and personal ratings.

### Structure
- `DetailsScreen.kt` — Route + arg definitions (`contentId: Int`, `mediaType: String`)
- `DetailsEvents.kt` — `FetchDetails`, `ToggleContentFromList(listId)`, `OnError`, `OnSnackbarDismiss`
- `DetailsInteractor.kt` — 13 methods for all detail sections + watchlist + ratings
- `DetailsViewModel.kt` — Constructor-injected `contentId` + `mediaType`, orchestrates multi-section loading
- `DetailsView.kt` — Scroll-based poster fade, lazy content sections
- `DetailsState.kt` — `detailsInfo`, `detailsCast`, `detailsVideos`
- `DetailsSnackbarState.kt` — `listId`, `addedItem: Boolean`
- `moreoptions/` — `MoreOptionsTab` (Videos + Similar), `PersonMoreOptionsTab` (Credits + Images), `VideosList`
- `otherlists/` — `OtherListsBottomSheet`
- `showall/` — `ShowAllContentView`
- `components/` — `DetailsTopBar`, `DetailsDescription`, `ContentCredits`, `DetailsBodyPlaceholder`

### State
| Field | Type | Description |
|---|---|---|
| `loadState` | `DataLoadStatus` | Loading / Success / Failed |
| `contentDetails` | `DetailedContent?` | Poster, overview, genres, runtime, status |
| `contentCredits` | `List<ContentCast>` | Cast sorted by `order` field |
| `contentVideos` | `List<Videos>` | Trailers and clips |
| `contentSimilar` | `List<GenericContent>` | Recommendations (fallback: similar) |
| `personCredits` | `List<GenericContent>` | Person's acting credits (PERSON type only) |
| `personImages` | `List<PersonImage>` | Person's profile images (PERSON type only) |
| `contentInListStatus` | `Map<Int, Boolean>` | listId → is item in this list |
| `personalRating` | `Float?` | User's 0–10 rating, null if unrated |
| `allLists` | `List<ListItem>` | Available lists for OtherListsBottomSheet |
| `snackbarState` | `DetailsSnackbarState` | Added/removed feedback |

### Loading Sequence
1. `init` → load `allLists`, `fetchPersonalRating()`, `initFetchDetails()`
2. `fetchDetails()` → `getContentDetailsById()`, `verifyContentInLists()`
3. `fetchCastDetails()` → sets `loadState = Success`
4. `fetchAdditionalInfo()` (runs after cast loaded):
   - **MOVIE/SHOW:** videos, recommendations (→ similar if empty)
   - **PERSON:** credits, images

### Special Behaviors
- **Scroll-based header fade:** `initialTitlePosY` tracked; `mapValueToRange()` calculates poster alpha 0→1 as user scrolls
- **iOS over-scroll:** Poster scales up when scrolled past top
- **Streaming providers:** Filtered by `LanguageManager.getUserCountryCode()`, flatrate providers only
- **Personal rating:** 0–10 range, 0.1 step, blue slider; stored in `PersonalRatingRepository`
- **Cast filtering:** Excludes entries with empty `profilePoster`, sorted by `order` ascending
- **Recommendations fallback:** If recommendations endpoint returns empty, falls back to similar endpoint
- **Watchlist toggle snackbar:** 500ms delay (`DELAY_UPDATE_POPUP_TEXT_MS`) before updating `contentInListStatus` to show feedback first
- **Multi-list support:** `OtherListsBottomSheet` shows all lists with checkmarks; `contentInListStatus` map tracks membership per list
- **ViewModel injection:** Uses `koinViewModel { parametersOf(contentId, mediaType) }` — `contentId` and `mediaType` are constructor parameters
