# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Maintenance Rules

- **Whenever a file or folder is added, removed, or moved in this project, update the Folder Tree section below to keep it in sync.**
- **When moving/renaming a file, use `git mv` instead of deleting and re-creating it, so that git preserves the file's commit history.**

## Kotlin Coding Rules

- **Never use fully-qualified references in code bodies.** Always add a proper `import` at the top of the file and use the short name (e.g., `mockk()` not `io.mockk.mockk()`).
- **Never use wildcard imports** (`import foo.bar.*`). Import each symbol explicitly.

## Project Overview

CineTracker is a Kotlin Multiplatform (KMP) movie & show tracker app using Compose Multiplatform for Android and iOS. It integrates with [The Movie DB API](https://developer.themoviedb.org/docs/getting-started) for content data.

## Build & Run

**Prerequisites:** TMDB API key in `local.properties` as `API_KEY=<your_key>` (injected at build time via BuildKonfig).

```bash
# Build Android
./gradlew :composeApp:assembleDebug

# Build release (has ProGuard enabled)
./gradlew :composeApp:assembleRelease

# Run all checks
./gradlew :composeApp:check
```

iOS is built via Xcode from the `iosApp/` directory using the shared KMP framework.

## Folder Tree

> Excludes: `.git/`, `.gradle/`, `.kotlin/`, `build/`, `.idea/`, `Pods/`, `schemas/`, generated Xcode project files, resources (mipmaps/webp/xcassets).

```
CineTracker-KMP/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── CLAUDE.md
├── README.md
├── knowledgeBackup/
│   └── InstructionsForFutureRefactor.md
│
├── composeApp/
│   ├── build.gradle.kts
│   ├── google-services.json
│   ├── proguard-rules.pro
│   │
│   ├── src/commonMain/
│   │   ├── composeResources/
│   │   │   ├── drawable/                          # Vector icons & logo (xml/webp)
│   │   │   ├── files/
│   │   │   │   └── erroranimation.json
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       ├── values-es-rES/strings.xml
│   │   │       ├── values-es-rMX/strings.xml
│   │   │       └── values-pt/strings.xml
│   │   │
│   │   └── kotlin/
│   │       ├── MainAppView.kt
│   │       ├── core/
│   │       │   ├── ImageLoader.kt
│   │       │   ├── LanguageManager.kt
│   │       │   └── di/
│   │       │       ├── KoinInitializer.kt
│   │       │       └── modules/
│   │       │           ├── InteractorModule.kt
│   │       │           └── ViewModelModule.kt
│   │       ├── common/
│   │       │   ├── domain/models/
│   │       │   │   ├── content/
│   │       │   │   │   ├── BaseMediaContent.kt
│   │       │   │   │   ├── ContentCast.kt
│   │       │   │   │   ├── DetailedContent.kt
│   │       │   │   │   ├── GenericContent.kt
│   │       │   │   │   ├── StreamProvider.kt
│   │       │   │   │   └── Videos.kt
│   │       │   │   ├── list/
│   │       │   │   │   └── ListItem.kt
│   │       │   │   ├── person/
│   │       │   │   │   ├── PersonDetails.kt
│   │       │   │   │   └── PersonImage.kt
│   │       │   │   └── util/
│   │       │   │       ├── ContentListType.kt
│   │       │   │       ├── DataLoadStatus.kt
│   │       │   │       ├── MediaType.kt
│   │       │   │       ├── SnackbarState.kt
│   │       │   │       └── SortTypeItem.kt
│   │       │   ├── ui/
│   │       │   │   ├── MainViewModel.kt
│   │       │   │   ├── components/
│   │       │   │   │   ├── ClassicGradientBrush.kt
│   │       │   │   │   ├── ClassicLoadingIndicator.kt
│   │       │   │   │   ├── GridContentList.kt
│   │       │   │   │   ├── NetworkImage.kt
│   │       │   │   │   ├── PlaceholderView.kt
│   │       │   │   │   ├── RatingComponent.kt
│   │       │   │   │   ├── SystemUtil.kt
│   │       │   │   │   ├── bottomsheet/
│   │       │   │   │   │   ├── GenericBottomSheet.kt
│   │       │   │   │   │   ├── ModalComponents.kt
│   │       │   │   │   │   ├── RatingBottomSheet.kt
│   │       │   │   │   │   └── SortBottomSheetComponents.kt
│   │       │   │   │   ├── button/
│   │       │   │   │   │   ├── GenericButton.kt
│   │       │   │   │   │   ├── SimpleButton.kt
│   │       │   │   │   │   └── SortIconButton.kt
│   │       │   │   │   ├── card/
│   │       │   │   │   │   ├── DefaultContentCard.kt
│   │       │   │   │   │   ├── ImageContentCard.kt
│   │       │   │   │   │   ├── MediaTypeTag.kt
│   │       │   │   │   │   └── PersonImages.kt
│   │       │   │   │   ├── popup/
│   │       │   │   │   │   ├── GenericPopupMenu.kt
│   │       │   │   │   │   ├── PopupMenuItem.kt
│   │       │   │   │   │   └── Snackbar.kt
│   │       │   │   │   └── tab/
│   │       │   │   │       ├── GenericTabComponents.kt
│   │       │   │   │       └── TabItem.kt
│   │       │   │   ├── screen/
│   │       │   │   │   ├── ErrorScreen.kt
│   │       │   │   │   └── GenericErrorScreen.kt
│   │       │   │   └── theme/
│   │       │   │       ├── Color.kt
│   │       │   │       ├── Shape.kt
│   │       │   │       ├── Theme.kt
│   │       │   │       └── Type.kt
│   │       │   └── util/
│   │       │       ├── CardsUtil.kt
│   │       │       ├── Constants.kt
│   │       │       ├── ConversionUtil.kt
│   │       │       ├── Extensions.kt
│   │       │       ├── NestedScrollConnection.kt
│   │       │       ├── UiConstants.kt
│   │       │       └── platform/
│   │       │           ├── DateUtils.kt          # expect
│   │       │           ├── PlatformUtils.kt      # expect
│   │       │           ├── ScreenSizeInfo.kt     # expect
│   │       │           ├── StatusBarUpdate.kt    # expect
│   │       │           └── StringFormat.kt       # expect
│   │       ├── database/
│   │       │   ├── AppDatabase.kt
│   │       │   ├── dao/
│   │       │   │   ├── ContentEntityDao.kt
│   │       │   │   ├── ListEntityDao.kt
│   │       │   │   └── PersonalRatingDao.kt
│   │       │   ├── di/
│   │       │   │   ├── DaoModule.kt
│   │       │   │   ├── DatabaseModule.kt         # expect
│   │       │   │   └── DatabaseRepositoryModule.kt
│   │       │   ├── model/
│   │       │   │   ├── ContentEntity.kt
│   │       │   │   ├── ListEntity.kt
│   │       │   │   └── PersonalRatingEntity.kt
│   │       │   └── repository/
│   │       │       ├── DatabaseRepository.kt
│   │       │       ├── DatabaseRepositoryImpl.kt
│   │       │       ├── PersonalRatingRepository.kt
│   │       │       └── PersonalRatingRepositoryImpl.kt
│   │       ├── features/
│   │       │   ├── browse/
│   │       │   │   ├── BrowseScreen.kt
│   │       │   │   ├── domain/
│   │       │   │   │   └── BrowseInteractor.kt
│   │       │   │   ├── events/
│   │       │   │   │   └── BrowseEvent.kt
│   │       │   │   └── ui/
│   │       │   │       ├── BrowseView.kt
│   │       │   │       ├── BrowseViewModel.kt
│   │       │   │       ├── components/
│   │       │   │       │   ├── BrowseSortBottomSheet.kt
│   │       │   │       │   ├── CollapsingTabRow.kt
│   │       │   │       │   └── MediaTypeTabItem.kt
│   │       │   │       └── paging/
│   │       │   │           └── MediaContentPagingSource.kt
│   │       │   ├── details/
│   │       │   │   ├── DetailsScreen.kt
│   │       │   │   ├── domain/
│   │       │   │   │   └── DetailsInteractor.kt
│   │       │   │   ├── events/
│   │       │   │   │   └── DetailsEvents.kt
│   │       │   │   ├── state/
│   │       │   │   │   ├── DetailsSnackbarState.kt
│   │       │   │   │   └── DetailsState.kt
│   │       │   │   └── ui/
│   │       │   │       ├── DetailsView.kt
│   │       │   │       ├── DetailsViewModel.kt
│   │       │   │       ├── components/
│   │       │   │       │   ├── ContentCredits.kt
│   │       │   │       │   ├── DetailsBodyPlaceholder.kt
│   │       │   │       │   ├── DetailsDescription.kt
│   │       │   │       │   ├── DetailsTopBar.kt
│   │       │   │       │   ├── moreoptions/
│   │       │   │       │   │   ├── MoreOptionsTab.kt
│   │       │   │       │   │   ├── MoreOptionsTabItem.kt
│   │       │   │       │   │   ├── PersonMoreOptionsTab.kt
│   │       │   │       │   │   └── VideosList.kt
│   │       │   │       │   ├── otherlists/
│   │       │   │       │   │   └── OtherListsBottomSheet.kt
│   │       │   │       │   └── showall/
│   │       │   │       │       └── ShowAllContentView.kt
│   │       │   │       └── util/
│   │       │   │           └── Extensions.kt
│   │       │   ├── home/
│   │       │   │   ├── HomeScreen.kt
│   │       │   │   ├── domain/
│   │       │   │   │   └── HomeInteractor.kt
│   │       │   │   ├── events/
│   │       │   │   │   └── HomeEvent.kt
│   │       │   │   └── ui/
│   │       │   │       ├── HomeView.kt
│   │       │   │       ├── HomeViewModel.kt
│   │       │   │       ├── components/
│   │       │   │       │   ├── carousel/
│   │       │   │       │   │   ├── ComingSoonContainer.kt
│   │       │   │       │   │   ├── TrendingContainer.kt
│   │       │   │       │   │   └── WatchlistContainer.kt
│   │       │   │       │   └── featured/
│   │       │   │       │       ├── FeaturedContainer.kt
│   │       │   │       │       ├── PersonFeaturedContainer.kt
│   │       │   │       │       └── SecondaryFeaturedContainer.kt
│   │       │   │       └── state/
│   │       │   │           └── HomeState.kt
│   │       │   ├── search/
│   │       │   │   ├── SearchScreen.kt
│   │       │   │   ├── domain/
│   │       │   │   │   └── SearchInteractor.kt
│   │       │   │   ├── events/
│   │       │   │   │   └── SearchEvent.kt
│   │       │   │   └── ui/
│   │       │   │       ├── SearchView.kt
│   │       │   │       ├── SearchViewModel.kt
│   │       │   │       ├── components/
│   │       │   │       │   ├── SearchBar.kt
│   │       │   │       │   ├── SearchFilters.kt
│   │       │   │       │   ├── SearchResults.kt
│   │       │   │       │   └── SearchTypeFilterItem.kt
│   │       │   │       └── paging/
│   │       │   │           └── SearchPagingSource.kt
│   │       │   └── watchlist/
│   │       │       ├── WatchlistScreen.kt
│   │       │       ├── domain/
│   │       │       │   └── WatchlistInteractor.kt
│   │       │       ├── events/
│   │       │       │   └── WatchlistEvent.kt
│   │       │       └── ui/
│   │       │           ├── WatchlistView.kt
│   │       │           ├── WatchlistViewModel.kt
│   │       │           ├── components/
│   │       │           │   ├── CreateListBottomSheet.kt
│   │       │           │   ├── DeleteListDialog.kt
│   │       │           │   ├── ListRemovePopupMenu.kt
│   │       │           │   ├── WatchlistBodyPlaceholder.kt
│   │       │           │   ├── WatchlistBottomSheet.kt
│   │       │           │   ├── WatchlistCardOptions.kt
│   │       │           │   ├── WatchlistCards.kt
│   │       │           │   └── WatchlistTabItem.kt
│   │       │           ├── model/
│   │       │           │   ├── DefaultLists.kt
│   │       │           │   ├── WatchlistItemAction.kt
│   │       │           │   └── WatchlistRatingSort.kt
│   │       │           └── state/
│   │       │               ├── WatchlistSnackbarState.kt
│   │       │               └── WatchlistState.kt
│   │       ├── navigation/
│   │       │   ├── MainNavGraph.kt
│   │       │   ├── Screen.kt
│   │       │   ├── ScreenUI.kt
│   │       │   ├── components/
│   │       │   │   ├── MainNavBar.kt
│   │       │   │   ├── MainNavBarItem.kt
│   │       │   │   └── TopNavBar.kt
│   │       │   └── screens/
│   │       │       ├── BrowseScreenUI.kt
│   │       │       ├── DetailsScreenUI.kt
│   │       │       ├── ErrorScreenUI.kt
│   │       │       ├── HomeScreenUI.kt
│   │       │       ├── SearchScreenUI.kt
│   │       │       └── WatchlistScreenUI.kt
│   │       └── network/
│   │           ├── NetworkClient.kt              # expect
│   │           ├── di/
│   │           │   ├── ApiModule.kt              # expect
│   │           │   ├── RepositoryModule.kt
│   │           │   └── ServiceModule.kt
│   │           ├── models/
│   │           │   ├── ApiError.kt
│   │           │   ├── content/
│   │           │   │   ├── common/
│   │           │   │   │   ├── BaseContentResponse.kt
│   │           │   │   │   ├── ContentCastResponse.kt
│   │           │   │   │   ├── ContentCreditsResponse.kt
│   │           │   │   │   ├── ContentGenre.kt
│   │           │   │   │   ├── ProductionCountry.kt
│   │           │   │   │   ├── VideoResponse.kt
│   │           │   │   │   ├── VideosByIdResponse.kt
│   │           │   │   │   └── WatchProvidersResponse.kt
│   │           │   │   ├── person/
│   │           │   │   │   ├── CrewResponse.kt
│   │           │   │   │   ├── PersonCreditsResponse.kt
│   │           │   │   │   ├── PersonImagesResponse.kt
│   │           │   │   │   └── PersonProfileResponse.kt
│   │           │   │   └── search/
│   │           │   │       └── ContentPagingResponse.kt
│   │           │   ├── repository/
│   │           │   │   ├── home/
│   │           │   │   │   ├── HomeRepository.kt
│   │           │   │   │   └── HomeRepositoryImpl.kt
│   │           │   │   ├── movie/
│   │           │   │   │   ├── MovieRepository.kt
│   │           │   │   │   └── MovieRepositoryImpl.kt
│   │           │   │   ├── person/
│   │           │   │   │   ├── PersonRepository.kt
│   │           │   │   │   └── PersonRepositoryImpl.kt
│   │           │   │   ├── search/
│   │           │   │   │   ├── SearchRepository.kt
│   │           │   │   │   └── SearchRepositoryImpl.kt
│   │           │   │   └── show/
│   │           │   │       ├── ShowRepository.kt
│   │           │   │       └── ShowRepositoryImpl.kt
│   │           │   └── services/
│   │           │       ├── home/
│   │           │       │   ├── HomeService.kt
│   │           │       │   └── HomeServiceImpl.kt
│   │           │       ├── movie/
│   │           │       │   ├── MovieService.kt
│   │           │       │   └── MovieServiceImpl.kt
│   │           │       ├── person/
│   │           │       │   ├── PersonService.kt
│   │           │       │   └── PersonServiceImpl.kt
│   │           │       ├── search/
│   │           │       │   ├── SearchService.kt
│   │           │       │   └── SearchServiceImpl.kt
│   │           │       └── show/
│   │           │           ├── ShowService.kt
│   │           │           └── ShowServiceImpl.kt
│   │           └── util/
│   │               ├── ApiResult.kt
│   │               ├── Either.kt
│   │               ├── NetworkExtensions.kt
│   │               └── Parameters.kt
│   │
│   ├── src/androidMain/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/
│   │   │   ├── com/projects/cinetracker/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── core/
│   │   │   │       └── CoreApplication.kt
│   │   │   ├── common/util/platform/
│   │   │   │   ├── DateUtils.kt
│   │   │   │   ├── PlatformUtils.kt
│   │   │   │   ├── ScreenSizeInfo.android.kt
│   │   │   │   ├── StatusBarUpdate.android.kt
│   │   │   │   └── StringFormat.kt
│   │   │   ├── core/di/
│   │   │   │   └── KoinInitializer.android.kt
│   │   │   ├── database/
│   │   │   │   ├── di/
│   │   │   │   │   └── DatabaseModule.android.kt
│   │   │   │   └── migration/
│   │   │   │       └── MigrationSchemas.kt
│   │   │   └── network/
│   │   │       ├── di/
│   │   │       │   └── ApiModule.android.kt
│   │   │       └── NetworkClient.android.kt
│   │   └── res/
│   │       └── values/
│   │           ├── colors.xml
│   │           ├── strings.xml
│   │           └── themes.xml
│   │
│   ├── src/iosMain/kotlin/
│   │   ├── MainViewController.kt
│   │   ├── common/util/platform/
│   │   │   ├── DateUtils.kt
│   │   │   ├── PlatformUtils.kt
│   │   │   ├── ScreenSizeInfo.ios.kt
│   │   │   ├── StatusBarUpdate.ios.kt
│   │   │   └── StringFormat.kt
│   │   ├── core/di/
│   │   │   └── KoinInitializer.ios.kt
│   │   ├── database/di/
│   │   │   └── DatabaseModule.ios.kt
│   │   └── network/
│   │       ├── di/
│   │       │   └── ApiModule.ios.kt
│   │       └── NetworkClient.ios.kt
│   │
│   ├── src/androidUnitTest/kotlin/
│   │   ├── common/
│   │   │   ├── domain/models/content/
│   │   │   │   ├── GenericContentMapperTest.kt
│   │   │   │   └── DetailedContentMapperTest.kt
│   │   │   ├── ui/
│   │   │   │   └── MainViewModelTest.kt
│   │   │   └── util/
│   │   │       └── TestFixtures.kt              # Shared test helpers (flows, entities, responses)
│   │   ├── database/
│   │   │   └── repository/
│   │   │       ├── DatabaseRepositoryImplTest.kt
│   │   │       └── PersonalRatingRepositoryImplTest.kt
│   │   └── features/
│   │       ├── details/
│   │       │   ├── util/
│   │       │   │   └── DetailsTestFixtures.kt   # Details-specific fixtures
│   │       │   ├── domain/
│   │       │   │   └── DetailsInteractorTest.kt
│   │       │   └── ui/
│   │       │       └── DetailsViewModelTest.kt
│   │       ├── watchlist/
│   │       │   ├── util/
│   │       │   │   └── WatchlistTestFixtures.kt # Watchlist-specific fixtures
│   │       │   ├── domain/
│   │       │   │   └── WatchlistInteractorTest.kt
│   │       │   └── ui/
│   │       │       └── WatchlistViewModelTest.kt
│   │       └── home/
│   │           ├── util/
│   │           │   └── HomeTestFixtures.kt      # Home-specific fixtures
│   │           ├── domain/
│   │           │   └── HomeInteractorTest.kt
│   │           └── ui/
│   │               └── HomeViewModelTest.kt
│   │       ├── browse/
│   │       │   └── ui/
│   │       │       ├── paging/
│   │       │       │   └── MediaContentPagingSourceTest.kt
│   │       │       └── BrowseViewModelTest.kt
│   │       └── search/
│   │           └── ui/
│   │               ├── paging/
│   │               │   └── SearchPagingSourceTest.kt
│   │               └── SearchViewModelTest.kt
│   │
│   └── src/debug/
│       └── google-services.json
│
└── iosApp/
    ├── Configuration/
    │   └── Config.xcconfig
    └── iosApp/
        ├── ContentView.swift
        ├── iOSApp.swift
        ├── Info.plist
        └── Util/
            └── Extensions.swift
```

## Architecture

**Pattern:** MVVM + Clean Architecture with Koin DI across a single `:composeApp` module.

**Source sets:**
- `commonMain` — All shared code (UI, business logic, networking, database)
- `androidMain` — Android platform implementations (OkHttp engine, Room DB path, Koin init via `CoreApplication`)
- `iosMain` — iOS platform implementations (Darwin engine, Room DB path, Koin init via `MainViewController`)

**Expect/actual declarations** are used for: `NetworkClient`, `DatabaseModule`, `ApiModule`, and platform utilities (`DateUtils`, `StringFormat`, `ScreenSizeInfo`, `PlatformUtils`, `StatusBarUpdate`).

### Layer Structure (all under `composeApp/src/commonMain/kotlin/`)

- **`features/`** — Feature modules (home, browse, watchlist, search, details), each containing:
  - `*Screen.kt` — Navigation route definition
  - `domain/` — Interactor (business logic)
  - `ui/` — ViewModel, composables, state classes, components
  - `events/` — UI event definitions
- **`navigation/`** — `Screen`/`ScreenUI` interfaces, `MainNavGraph`, nav bar components
- **`network/`** — Ktor client setup, services, repositories, DTOs (`models/`), `Either<Left,Right>` error handling
- **`database/`** — Room entities (`ContentEntity`, `ListEntity`, `PersonalRatingEntity`), DAOs, repository. DB version 6 with migrations in `androidMain`.
- **`common/`** — Shared UI components, domain models (`BaseMediaContent`, `GenericContent`, `DetailedContent`, `MediaType`), theme, platform utilities
- **`core/di/`** — Koin module definitions (`InteractorModule`, `ViewModelModule`)

### Key Patterns

- **Navigation:** Androidx Navigation Compose with route-based `Screen` interface. `ScreenUI` adapts screens to composables. 6 routes: Home, Browse, Watchlist, Search, Details, Error.
- **State management:** ViewModel + StateFlow. `MainViewModel` holds global state (sort type, media type, list management).
- **Networking:** Ktor with platform-specific engines. Base URL: `https://api.themoviedb.org/3/`. Error handling via `Either<ApiError, T>` and `ApiResult`.
- **Pagination:** Cash App Multiplatform Paging (page size 20) in Browse and Search features.
- **Image loading:** Coil 3 with Ktor integration.
- **Database:** Room Multiplatform with bundled SQLite. Default lists: "watchlist" and "watched". iOS uses destructive fallback migration.

### DI Module Organization

Koin modules are split by layer: `ApiModule` (expect/actual), `ServiceModule`, `RepositoryModule`, `DatabaseModule` (expect/actual), `DaoModule`, `DatabaseRepositoryModule`, `InteractorModule`, `ViewModelModule`.

## Key Types

- `MediaType` — enum: MOVIE, SHOW, PERSON, UNKNOWN
- `DataLoadStatus` — Loading, Success, Failed
- `GenericContent` / `DetailedContent` / `BaseMediaContent` — domain content models
- `ContentEntity` / `ListEntity` / `PersonalRatingEntity` — Room entities

## UI Style Guide

### Theme

**Dark theme only** — there is no light theme. All new UI must follow this dark palette.

### Color Palette

| Token | Hex | Usage |
|---|---|---|
| `PrimaryYellowColor` | `#FFFA9F26` | Primary accent — buttons, selected nav items, sort indicators, tab indicators, snackbar actions |
| `PrimaryYellowColor_90` | `#E6FA9F26` | Yellow at 90% opacity — `MediaTypeTag` background |
| `PrimaryBlueColor` | `#FF2682FA` | Secondary interactive — personal rating stars, rating slider thumb/track |
| `PrimaryBlackColor` | `#FF000000` | Background, top bar, Material `primary` |
| `PrimaryWhiteColor` | `#FFFFFFFF` | Primary text, icons (`onPrimary`) |
| `PrimaryGreyColor` | `#FFDCDCDC` | Tertiary text, unselected tab labels |
| `PrimaryGreyColor_55` | `#8C24252A` | Grey at ~55% opacity — `surfaceVariant` |
| `SecondaryGreyColor` | `#FF9D9D9D` | Muted text (rating labels, secondary info) |
| `MainBarGreyColor` | `#FF191A1D` | Nav bar, card backgrounds, bottom sheets, tab dividers |
| `PrimaryRedColor` | `#FFFA2626` | Error states |
| `PlaceholderGrey` / `PlaceholderGrey2` | `#FFBB5B5` / `#FF8F8B8B` | Shimmer placeholder gradient |
| `DividerGrey` | `#592C2E33` | Semi-transparent dividers |

### Typography

All fonts use `SansSerif` (system default). Key styles used across the app:

- **`headlineLarge`** (20sp, w420) — Top bar titles
- **`headlineMedium`** (18sp) — Bottom sheet headers
- **`titleMedium`** (18sp, w500) — Card titles, button text
- **`titleSmall`** (14sp, w500) — Tags, labels (`MediaTypeTag`)
- **`bodyMedium`** (16sp) — General content text
- **`bodySmall`** (14sp) — Popup menu items
- **`labelSmall`** (12sp) — Nav bar labels, small captions
- **`displayLarge`** (32sp) — Large display text

### Shapes

All shapes use `CARD_ROUND_CORNER = 4dp` as the base unit:
- `extraSmall`: 4dp bottom corners only (cards with top image)
- `small`: 4dp top corners only (image tops)
- `medium`: 6dp all corners
- `large`: 8dp all corners

### Spacing

**Padding:** `SMALL_PADDING = 4dp`, `DEFAULT_PADDING = 8dp`, `LARGE_PADDING = 12dp`
**Margins:** `SMALL_MARGIN = 8dp`, `DEFAULT_MARGIN = 16dp`, `LARGE_MARGIN = 20dp`
**Section gap:** `SECTION_PADDING = 24dp`
**Screen horizontal margin:** 16dp (consistently used across features)

### Component Sizing

- **Poster aspect ratio:** 2:3 (`POSTER_ASPECT_RATIO = 0.667f`, height = width × 1.5)
- **Backdrop aspect ratio:** 16:9
- **Card corners:** 4dp
- **Card elevation:** 2dp on `MainBarGreyColor` background
- **Nav bar height:** 60dp, icon + label stacked vertically
- **Top bar height:** 64dp
- **Carousel card width:** 150dp
- **Watchlist image width:** 100dp
- **Cast picture size:** 100dp (card height: 185dp)
- **Rating star:** 22dp default, 18dp in carousels
- **Stream provider icon:** 60dp
- **Sort icon:** 32dp
- **Smaller device breakpoint:** 380dp width

### Existing Reusable Components

Always prefer these over creating new ones:

- **`NetworkImage`** — Coil-backed image with shimmer placeholder. Params: `imageUrl`, `widthDp`, `heightDp`, `contentScale`, `alpha`.
- **`RatingComponent`** — Yellow star + rating text. Params: `rating`, `ratingIconSize`.
- **`PersonalRatingComponent`** — Blue star for user ratings, clickable, with optional clear.
- **`DefaultContentCard`** — Poster card with image, title, and rating. Used in carousels.
- **`ImageContentCard`** — Image-only card for grids.
- **`MediaTypeTag`** — Yellow badge showing "Movie" or "TV Show".
- **`GenericButton`** — Yellow background, black text, 4dp corners.
- **`SimpleButton`** — Text-only button, yellow by default.
- **`SortIconButton`** — Sort icon that turns yellow when active.
- **`GenericBottomSheet`** — Standard bottom sheet with `MainBarGreyColor` background and header.
- **`RatingBottomSheet`** — Slider (0–10, 0.1 steps), blue thumb, save/clear actions.
- **`GenericTabComponents`** — Scrollable tab row with yellow indicator, max 160dp per tab.
- **`GridContentList`** — Responsive grid layout (min card width 450dp, dynamic columns).
- **`ClassicLoadingIndicator`** — Centered circular progress in yellow.
- **`ComponentPlaceholder`** — Shimmer animation (3s, infinite, linear gradient).
- **`ClassicVerticalGradientBrush`** — Directional gradient modifier (UP/DOWN/LEFT/RIGHT).
- **`GenericPopupMenu`** — Dropdown menu with black background.
- **`Snackbar`** — Grey container, black text, yellow undo action.

### UI Patterns

- **Cards:** `MainBarGreyColor` background + 2dp elevation. Image on top (cropped, 1.5× aspect), text below.
- **Selected state:** Yellow color for icons and text. Unselected uses white or grey.
- **Loading:** Shimmer placeholders for images; centered yellow circular indicator for pages.
- **Empty states:** Centered text with ~30% top padding offset.
- **Details screen:** Full-width poster with scroll-based alpha fade, content offset at 70% of poster height.
- **Home screen:** Featured poster at full width, followed by horizontal carousels (150dp cards).
- **Gradients:** Black-to-transparent overlays on poster images for text readability.
- **Scroll behavior:** Collapsing tab rows in Browse; nested scroll connections for coordinated scrolling.
- **Responsive:** Logo scales to 50% width on screens < 380dp; grid columns adjust dynamically.

## Version Catalog

Dependencies are managed in `gradle/libs.versions.toml`. Key versions: Kotlin 2.0.20, Compose 1.7.0, Ktor 2.3.12, Room 2.7.0-alpha02, Koin 3.5.6.

## Android Config

Package: `com.projects.cinetracker`, App ID: `gustavo.projects.restapi`, minSdk 24, targetSdk 35.
