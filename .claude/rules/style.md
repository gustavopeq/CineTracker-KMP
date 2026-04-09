# UI Style Guide

## Theme

**Dark theme only** — there is no light theme. All new UI must follow this dark palette.

## Color Palette

Defined in `Color.kt` as top-level `val` declarations.

| Token | Value | Role |
|---|---|---|
| `PrimaryYellowColor` | `#FFFA9F26` | Primary accent (buttons, selected nav, tab indicators, snackbar actions) |
| `PrimaryYellowColor_90` | `#E6FA9F26` | Yellow at 90% opacity (MediaTypeTag background) |
| `PrimaryBlueColor` | `#FF2682FA` | Secondary interactive (personal rating stars, rating slider) |
| `PrimaryBlackColor` | `#FF000000` | App background, top bar, Material3 `primary` |
| `PrimaryWhiteColor` | `#FFFFFFFF` | Primary text, icons (Material3 `onPrimary`) |
| `PrimaryGreyColor` | `#FFDCDCDC` | Tertiary text, unselected tab labels (Material3 `tertiary`) |
| `PrimaryGreyColor_55` | `#8C24252A` | Grey at ~55% opacity (Material3 `surfaceVariant`) |
| `SecondaryGreyColor` | `#FF9D9D9D` | Muted text, rating labels (Material3 `surface`) |
| `MainBarGreyColor` | `#FF191A1D` | Nav bar, card backgrounds, bottom sheets |
| `PrimaryRedColor` | `#FFFA2626` | Error states (Material3 `error`) |
| `placeholderGrey` | `#FFB8B5B5` | Shimmer placeholder gradient start |
| `placeholderGrey2` | `#FF8F8B8B` | Shimmer placeholder gradient end |
| `DividerGrey` | `#592C2E33` | Semi-transparent dividers (Material3 `inverseSurface`) |

### Material3 Color Scheme Mapping

Defined in `Theme.kt`. **Important:** Many components reference custom color tokens directly (e.g., `MainBarGreyColor`) rather than going through `MaterialTheme.colorScheme`. When mapping designs, match by hex value, not by Material3 role name.

```
darkColorScheme(
    primary         = PrimaryBlackColor      // #000000
    onPrimary       = PrimaryWhiteColor      // #FFFFFF
    secondary       = PrimaryYellowColor     // #FA9F26
    onSecondary     = PrimaryBlackColor      // #000000
    tertiary        = PrimaryGreyColor       // #DCDCDC
    surface         = SecondaryGreyColor     // #9D9D9D
    onSurface       = PrimaryWhiteColor      // #FFFFFF
    onSurfaceVariant= PrimaryYellowColor     // #FA9F26
    inverseSurface  = DividerGrey            // #592C2E33
    surfaceVariant  = PrimaryGreyColor_55    // #8C24252A
    background      = PrimaryBlackColor      // #000000
    error           = PrimaryRedColor        // #FA2626
)
```

## Typography

Defined in `Type.kt` as `AppTypography`. All styles use `FontFamily.SansSerif`.

| Style | Size | Weight | Line Height | Usage |
|---|---|---|---|---|
| `displayLarge` | 32sp | 400 | 28sp | Large display text |
| `displayMedium` | 28sp | 400 | 28sp | Medium display |
| `displaySmall` | 23sp | 400 | 28sp | Small display |
| `headlineLarge` | 20sp | 420 | 28sp | Top bar titles |
| `headlineMedium` | 18sp | 400 | 28sp | Bottom sheet headers |
| `headlineSmall` | 16sp | 400 | 28sp | Section sub-headers |
| `titleLarge` | 22sp | 400 | 28sp | Large titles |
| `titleMedium` | 18sp | 500 | 24sp | Card titles, button text |
| `titleSmall` | 14sp | 500 | 24sp | Tags, labels (MediaTypeTag) |
| `bodyLarge` | 24sp | 400 | 24sp | **Known defect: larger than displaySmall** |
| `bodyMedium` | 16sp | 400 | 20sp | General content text |
| `bodySmall` | 14sp | 400 | 20sp | Popup menu items |
| `labelMedium` | 18sp | 400 | 24sp | Mid-size labels |
| `labelSmall` | 12sp | 400 | 20sp | Nav bar labels, small captions |

## Shapes

Defined in `Shape.kt` as `RoundCornerShapes`. Base unit: `CARD_ROUND_CORNER = 4dp`.

| Shape | Radius | Usage |
|---|---|---|
| `extraSmall` | 4dp bottom corners only | Cards with top image (text area below) |
| `small` | 4dp top corners only | Image tops in cards |
| `medium` | 6dp all corners | General rounded containers |
| `large` | 8dp all corners | Larger rounded containers |

## Spacing

Defined in `UiConstants.kt`. All values are raw `Int` or `Float` — append `.dp` in code.

**Padding:** `SMALL_PADDING = 4`, `DEFAULT_PADDING = 8`, `LARGE_PADDING = 12`
**Margins:** `SMALL_MARGIN = 8`, `DEFAULT_MARGIN = 16`, `LARGE_MARGIN = 20`
**Section gap:** `SECTION_PADDING = 24`
**System nav:** `SYSTEM_BOTTOM_NAV_PADDING = 32`
**Screen horizontal margin:** 16dp (consistently used across features)

## Component Sizing

| Constant | Value | Usage |
|---|---|---|
| `BUTTON_NAVIGATION_BAR_HEIGHT` | 60 | Bottom nav bar height (dp) |
| `RETURN_TOP_BAR_HEIGHT` | 64 | Top bar height (dp) |
| `POSTER_ASPECT_RATIO` | 0.667f | Width:height ratio (2:3) |
| `POSTER_ASPECT_RATIO_MULTIPLY` | 1.5f | Height = width x 1.5 |
| `BACKDROP_ASPECT_RATIO` | 16/9f | Backdrop images |
| `RATING_STAR_DEFAULT_SIZE` | 22 | Default star icon size (dp) |
| `CAROUSEL_RATING_STAR_SIZE` | 18 | Star size in carousels (dp) |
| `BROWSE_CARD_DEFAULT_ELEVATION` | 2 | Card elevation (dp) |
| `BROWSE_SORT_ICON_SIZE` | 32 | Sort icon size (dp) |
| `BROWSE_MIN_CARD_WIDTH` | 450 | Grid min card width for column calc |
| `DETAILS_CAST_PICTURE_SIZE` | 100 | Cast member image (dp) |
| `DETAILS_CAST_CARD_HEIGHT` | 185 | Cast card height (dp) |
| `STREAM_PROVIDER_ICON_SIZE` | 60 | Streaming provider icon (dp) |
| `CAROUSEL_CARDS_WIDTH` | 150 | Home carousel card width (dp) |
| `WATCHLIST_IMAGE_WIDTH` | 100 | Watchlist poster width (dp) |
| `SEARCH_FILTER_BUTTON_HEIGHT` | 30 | Search filter chip height (dp) |
| `GENERIC_TAB_MAX_WIDTH` | 160 | Max tab width (dp) |
| `SMALLER_DEVICES_WIDTH` | 380 | Breakpoint for smaller devices (dp) |

## Component Library

All reusable components live in `common/ui/components/`. Always prefer these over creating new ones.

### Image & Media
| Component | File | Key Params | Notes |
|---|---|---|---|
| `NetworkImage` | `NetworkImage.kt` | `imageUrl`, `widthDp`, `heightDp`, `contentScale`, `alpha` | Coil3-backed with shimmer placeholder |
| `ComponentPlaceholder` | `PlaceholderView.kt` | `modifier` | Shimmer animation (gradient between `placeholderGrey` and `placeholderGrey2`) |
| `ClassicVerticalGradientBrush` | `ClassicGradientBrush.kt` | direction (UP/DOWN/LEFT/RIGHT) | Black-to-transparent overlay |

### Cards
| Component | File | Key Params | Notes |
|---|---|---|---|
| `DefaultContentCard` | `card/DefaultContentCard.kt` | `cardWidth`, `imageUrl`, `title`, `rating`, `goToDetails` | Poster + title + rating. `MainBarGreyColor` bg, 2dp elevation |
| `ImageContentCard` | `card/ImageContentCard.kt` | -- | Image-only card for grids |
| `MediaTypeTag` | `card/MediaTypeTag.kt` | -- | Yellow badge ("Movie" / "TV Show"), `PrimaryYellowColor_90` bg |
| `PersonImages` | `card/PersonImages.kt` | -- | Person profile image cards |

### Buttons
| Component | File | Key Params | Notes |
|---|---|---|---|
| `GenericButton` | `button/GenericButton.kt` | `buttonText`, `enabled`, `onClick` | Yellow bg (`secondary`), black text (`primary`), 4dp corners |
| `SimpleButton` | `button/SimpleButton.kt` | -- | Text-only button, yellow by default |
| `SortIconButton` | `button/SortIconButton.kt` | -- | Sort icon that turns yellow when active |

### Bottom Sheets
| Component | File | Notes |
|---|---|---|
| `GenericBottomSheet` | `bottomsheet/GenericBottomSheet.kt` | `MainBarGreyColor` bg with header |
| `RatingBottomSheet` | `bottomsheet/RatingBottomSheet.kt` | Slider 0-10, 0.1 steps, blue thumb, save/clear |
| `SortBottomSheetComponents` | `bottomsheet/SortBottomSheetComponents.kt` | Sort option rows |
| `ModalComponents` | `bottomsheet/ModalComponents.kt` | Shared modal building blocks |

### Tabs
| Component | File | Notes |
|---|---|---|
| `GenericTabComponents` | `tab/GenericTabComponents.kt` | Scrollable tab row, yellow indicator, max 160dp/tab |
| `TabItem` | `tab/TabItem.kt` | Tab data model |

### Feedback & Status
| Component | File | Notes |
|---|---|---|
| `RatingComponent` | `RatingComponent.kt` | Yellow star + rating text |
| `PersonalRatingComponent` | `RatingComponent.kt` | Blue star for user ratings, clickable |
| `ClassicLoadingIndicator` | `ClassicLoadingIndicator.kt` | Centered circular progress, yellow |
| `Snackbar` | `popup/Snackbar.kt` | Grey container, black text, yellow undo action |
| `GenericPopupMenu` | `popup/GenericPopupMenu.kt` | Dropdown, black background |
| `PopupMenuItem` | `popup/PopupMenuItem.kt` | Individual popup row |

### Layout
| Component | File | Notes |
|---|---|---|
| `GridContentList` | `GridContentList.kt` | Responsive grid (dynamic columns based on screen width) |

## Icon System

Icons are stored as XML vector drawables in `composeResources/drawable/` with `ic_` prefix (snake_case). Accessed via:
```kotlin
painterResource(resource = Res.drawable.ic_star)
```

## System Bars

Both platforms run edge-to-edge. Bar colors are managed by `SystemBarsContainer` in `MainAppView.kt` — never set bar colors from individual screens or platform-specific code.

### Status Bar Color

| Screen | Color |
|--------|-------|
| Search | `MainBarGreyColor` (merges with search bar) |
| All others | `PrimaryBlackColor` |

### Navigation Bar Color

| Screen | Color |
|--------|-------|
| Standalone (Details, Error) | `PrimaryBlackColor` |
| All others | `MainBarGreyColor` (merges with bottom nav bar) |

### Edge-to-Edge Setup

- **Android:** `enableEdgeToEdge()` in `MainActivity` makes system bars transparent; Compose draws behind them.
- **iOS:** `ContentView.swift` uses `.ignoresSafeArea(edges: .all)` so ComposeView fills the entire screen.
- **Scaffold** uses `contentWindowInsets = WindowInsets(0, 0, 0, 0)` to prevent double inset application on iOS.

## Platform Touch Target Guidelines

- **Minimum touch target size:** 48dp on Android (Material Design), 44pt on iOS (HIG). Every tappable element must meet this minimum — use `Modifier.padding` or `Modifier.size` to achieve it if the visual element is smaller.
- **Spacing between tappable elements:** At least 8dp between adjacent touch targets to prevent accidental taps.
- **Text-only clickable items** (links, inline actions) must have enough vertical padding to reach 48dp total tap area (e.g., `padding(vertical = 16.dp)` for a 16sp text element).
- **No click ripple on text links** — use `Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() })` for inline text links and ghost buttons where ripple looks out of place.

## UI Patterns

- **Cards:** `MainBarGreyColor` background + 2dp elevation. Image on top (cropped, 1.5x aspect), text below.
- **Selected state:** Yellow color for icons and text. Unselected uses white or grey.
- **Loading:** Shimmer placeholders for images; centered yellow circular indicator for pages.
- **Empty states:** Centered text with ~30% top padding offset.
- **Details screen:** Full-width poster with scroll-based alpha fade, content offset at 70% of poster height.
- **Home screen:** Featured poster at full width, followed by horizontal carousels (150dp cards).
- **Gradients:** Black-to-transparent overlays on poster images for text readability.
- **Scroll behavior:** Collapsing tab rows in Browse; nested scroll connections for coordinated scrolling.
- **Responsive:** Logo scales to 50% width on screens < 380dp; grid columns adjust dynamically.

## Figma-to-Code Mapping Rules

When translating Figma designs to this codebase:

1. **Match colors by hex value**, not by Material3 role name. The color scheme mapping is non-standard.
2. **Match typography by font size and weight**, not by Material3 style name. The type scale has known hierarchy inversions.
3. **Reuse existing components** before creating new ones. Check the component library above.
4. **Use `UiConstants`** for all spacing and sizing values. Do not introduce magic numbers.
5. **Use `NetworkImage`** for all remote images. Never use Coil directly.
6. **Cards always use** `MainBarGreyColor` background with 2dp elevation.
7. **Buttons use** `MaterialTheme.colorScheme.secondary` (yellow) for container, `.primary` (black) for text.
8. **All corners** use `CARD_ROUND_CORNER` (4dp) as the base unit, accessed via `RoundCornerShapes`.
9. **Poster images** always maintain 2:3 aspect ratio (`POSTER_ASPECT_RATIO`).
10. **Icons** must be added as XML vector drawables in `composeResources/drawable/` with `ic_` prefix.
