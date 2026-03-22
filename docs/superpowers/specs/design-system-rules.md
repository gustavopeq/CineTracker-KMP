# CineTracker Design System Rules

> Auto-generated design system rules for Figma-to-code integration via MCP.
> Framework: Compose Multiplatform + Material3 | Language: Kotlin

---

## 1. Token Definitions

### 1.1 Colors

Defined in `composeApp/src/commonMain/kotlin/common/ui/theme/Color.kt` as top-level `val` declarations using `androidx.compose.ui.graphics.Color`.

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

### 1.2 Material3 Color Scheme Mapping

Defined in `composeApp/src/commonMain/kotlin/common/ui/theme/Theme.kt`. Dark theme only -- no light theme exists.

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

**Important:** Many components reference custom color tokens directly (e.g., `MainBarGreyColor`) rather than going through `MaterialTheme.colorScheme`. When mapping Figma designs, match by hex value, not by Material3 role name.

### 1.3 Typography

Defined in `composeApp/src/commonMain/kotlin/common/ui/theme/Type.kt` as `AppTypography` (Material3 `Typography`). All styles use `FontFamily.SansSerif`.

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
| `bodyLarge` | 24sp | 400 | 24sp | **Note: larger than displaySmall** |
| `bodyMedium` | 16sp | 400 | 20sp | General content text |
| `bodySmall` | 14sp | 400 | 20sp | Popup menu items |
| `labelMedium` | 18sp | 400 | 24sp | Mid-size labels |
| `labelSmall` | 12sp | 400 | 20sp | Nav bar labels, small captions |

**Known defect:** `bodyLarge` (24sp) is larger than `displaySmall` (23sp), which breaks standard Material3 type hierarchy. When mapping from Figma, match by font size rather than by Material3 role name.

### 1.4 Shapes

Defined in `composeApp/src/commonMain/kotlin/common/ui/theme/Shape.kt` as `RoundCornerShapes` (Material3 `Shapes`). Base unit: `CARD_ROUND_CORNER = 4dp`.

| Shape | Radius | Usage |
|---|---|---|
| `extraSmall` | 4dp bottom corners only | Cards with top image (text area below) |
| `small` | 4dp top corners only | Image tops in cards |
| `medium` | 6dp all corners | General rounded containers |
| `large` | 8dp all corners | Larger rounded containers |

### 1.5 Spacing & Sizing Constants

Defined in `composeApp/src/commonMain/kotlin/common/util/UiConstants.kt` as `object UiConstants` with `const val` members. All values are raw `Int` or `Float` -- append `.dp`, `.sp`, or use as multipliers in code.

#### Padding & Margins
| Constant | Value | Usage |
|---|---|---|
| `SMALL_PADDING` | 4 | Tight internal padding |
| `DEFAULT_PADDING` | 8 | Standard internal padding |
| `LARGE_PADDING` | 12 | Generous internal padding |
| `SMALL_MARGIN` | 8 | Tight outer spacing |
| `DEFAULT_MARGIN` | 16 | Standard outer spacing / screen horizontal margin |
| `LARGE_MARGIN` | 20 | Generous outer spacing |
| `SECTION_PADDING` | 24 | Gap between major sections |
| `SYSTEM_BOTTOM_NAV_PADDING` | 32 | Padding above system nav |

#### Component Sizing
| Constant | Value | Usage |
|---|---|---|
| `BUTTON_NAVIGATION_BAR_HEIGHT` | 60 | Bottom nav bar height (dp) |
| `RETURN_TOP_BAR_HEIGHT` | 64 | Top bar height (dp) |
| `CARD_ROUND_CORNER` | 4 | Base corner radius (dp) |
| `POSTER_ASPECT_RATIO` | 0.667f | Width:height ratio (2:3) |
| `POSTER_ASPECT_RATIO_MULTIPLY` | 1.5f | Height = width x 1.5 |
| `BACKDROP_ASPECT_RATIO` | 16/9f | Backdrop images |
| `RATING_STAR_DEFAULT_SIZE` | 22 | Default star icon size (dp) |
| `CAROUSEL_RATING_STAR_SIZE` | 18 | Star size in carousels (dp) |
| `CLASSIC_BUTTON_BORDER_SIZE` | 4 | Button corner radius (dp) |
| `BROWSE_CARD_DEFAULT_ELEVATION` | 2 | Card elevation (dp) |
| `BROWSE_SORT_ICON_SIZE` | 32 | Sort icon size (dp) |
| `BROWSE_MIN_CARD_WIDTH` | 450 | Grid min card width for column calc |
| `DETAILS_CAST_PICTURE_SIZE` | 100 | Cast member image (dp) |
| `DETAILS_CAST_CARD_HEIGHT` | 185 | Cast card height (dp) |
| `STREAM_PROVIDER_ICON_SIZE` | 60 | Streaming provider icon (dp) |
| `CAROUSEL_CARDS_WIDTH` | 150 | Home carousel card width (dp) |
| `WATCHLIST_IMAGE_WIDTH` | 100 | Watchlist poster width (dp) |
| `PERSON_FEATURED_IMAGE_WIDTH` | 150 | Person featured image (dp) |
| `SEARCH_FILTER_BUTTON_HEIGHT` | 30 | Search filter chip height (dp) |
| `GENERIC_TAB_MAX_WIDTH` | 160 | Max tab width (dp) |
| `SMALLER_DEVICES_WIDTH` | 380 | Breakpoint for smaller devices (dp) |
| `DETAILS_TITLE_IMAGE_OFFSET_PERCENT` | 0.7f | Content offset at 70% of poster |
| `MEDIA_TYPE_TAG_CORNER_SIZE` | 2 | Tag badge corner radius (dp) |

---

## 2. Component Library

All reusable UI components live in `composeApp/src/commonMain/kotlin/common/ui/components/`. They are `@Composable` functions following Compose conventions.

### 2.1 Image & Media
| Component | File | Key Params | Notes |
|---|---|---|---|
| `NetworkImage` | `NetworkImage.kt` | `imageUrl`, `widthDp`, `heightDp`, `contentScale`, `alpha` | Coil3-backed with shimmer placeholder |
| `ComponentPlaceholder` | `PlaceholderView.kt` | `modifier` | Shimmer animation (gradient between `placeholderGrey` and `placeholderGrey2`) |
| `ClassicVerticalGradientBrush` | `ClassicGradientBrush.kt` | direction (UP/DOWN/LEFT/RIGHT) | Black-to-transparent overlay |

### 2.2 Cards
| Component | File | Key Params | Notes |
|---|---|---|---|
| `DefaultContentCard` | `card/DefaultContentCard.kt` | `cardWidth`, `imageUrl`, `title`, `rating`, `goToDetails` | Poster + title + rating. `MainBarGreyColor` bg, 2dp elevation |
| `ImageContentCard` | `card/ImageContentCard.kt` | -- | Image-only card for grids |
| `MediaTypeTag` | `card/MediaTypeTag.kt` | -- | Yellow badge ("Movie" / "TV Show"), `PrimaryYellowColor_90` bg |
| `PersonImages` | `card/PersonImages.kt` | -- | Person profile image cards |

### 2.3 Buttons
| Component | File | Key Params | Notes |
|---|---|---|---|
| `GenericButton` | `button/GenericButton.kt` | `buttonText`, `enabled`, `onClick` | Yellow bg (`secondary`), black text (`primary`), 4dp corners |
| `SimpleButton` | `button/SimpleButton.kt` | -- | Text-only button, yellow by default |
| `SortIconButton` | `button/SortIconButton.kt` | -- | Sort icon that turns yellow when active |

### 2.4 Bottom Sheets
| Component | File | Notes |
|---|---|---|
| `GenericBottomSheet` | `bottomsheet/GenericBottomSheet.kt` | `MainBarGreyColor` bg with header |
| `RatingBottomSheet` | `bottomsheet/RatingBottomSheet.kt` | Slider 0-10, 0.1 steps, blue thumb, save/clear |
| `SortBottomSheetComponents` | `bottomsheet/SortBottomSheetComponents.kt` | Sort option rows |
| `ModalComponents` | `bottomsheet/ModalComponents.kt` | Shared modal building blocks |

### 2.5 Tabs
| Component | File | Notes |
|---|---|---|
| `GenericTabComponents` | `tab/GenericTabComponents.kt` | Scrollable tab row, yellow indicator, max 160dp/tab |
| `TabItem` | `tab/TabItem.kt` | Tab data model |

### 2.6 Feedback & Status
| Component | File | Notes |
|---|---|---|
| `RatingComponent` | `RatingComponent.kt` | Yellow star + rating text |
| `PersonalRatingComponent` | `RatingComponent.kt` | Blue star for user ratings, clickable |
| `ClassicLoadingIndicator` | `ClassicLoadingIndicator.kt` | Centered circular progress, yellow |
| `Snackbar` | `popup/Snackbar.kt` | Grey container, black text, yellow undo action |
| `GenericPopupMenu` | `popup/GenericPopupMenu.kt` | Dropdown, black background |
| `PopupMenuItem` | `popup/PopupMenuItem.kt` | Individual popup row |

### 2.7 Layout
| Component | File | Notes |
|---|---|---|
| `GridContentList` | `GridContentList.kt` | Responsive grid (dynamic columns based on screen width) |

---

## 3. Icon System

Icons are stored as XML vector drawables and one WebP logo in:
`composeApp/src/commonMain/composeResources/drawable/`

### Available Icons
| Resource | File | Usage |
|---|---|---|
| `ic_back_arrow` | `ic_back_arrow.xml` | Navigation back |
| `ic_check` | `ic_check.xml` | Checkmarks |
| `ic_chevron_right` | `ic_chevron_right.xml` | Forward/expand indicator |
| `ic_close` | `ic_close.xml` | Close/dismiss |
| `ic_more_options` | `ic_more_options.xml` | Overflow menu |
| `ic_nav_browse` | `ic_nav_browse.xml` | Browse tab icon |
| `ic_nav_home` | `ic_nav_home.xml` | Home tab icon |
| `ic_nav_search` | `ic_nav_search.xml` | Search tab icon |
| `ic_nav_watchlist` | `ic_nav_watchlist.xml` | Watchlist tab icon |
| `ic_outlined_star` | `ic_outlined_star.xml` | Empty star (unrated) |
| `ic_play_video` | `ic_play_video.xml` | Video play button |
| `ic_sort` | `ic_sort.xml` | Sort action |
| `ic_star` | `ic_star.xml` | Filled star (rated) |
| `ic_watchlist` | `ic_watchlist.xml` | Watchlist action |
| `ic_watchlist_add_list` | `ic_watchlist_add_list.xml` | Add to list action |
| `cinetracker_name_logo` | `cinetracker_name_logo.webp` | App logo |

### Usage Pattern
Icons are accessed via generated resource references:
```kotlin
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource

painterResource(resource = Res.drawable.ic_star)
```

Naming convention: `ic_` prefix for all icons, snake_case.

---

## 4. Styling Approach

### Theme Application
All UI is wrapped in `CineTrackerTheme` (defined in `Theme.kt`), which provides the Material3 `darkColorScheme`, `AppTypography`, and `RoundCornerShapes` through `MaterialTheme`.

### Color Usage Patterns
- **Via MaterialTheme:** `MaterialTheme.colorScheme.onPrimary` for white text, `.secondary` for yellow accent, `.primary` for black backgrounds
- **Direct token references:** Many components use tokens directly (e.g., `MainBarGreyColor` for card backgrounds, `PrimaryBlueColor` for personal ratings) rather than going through the color scheme

### Typography Usage
Always access via `MaterialTheme.typography`:
```kotlin
style = MaterialTheme.typography.titleMedium
```

### Responsive Design
- Grid columns adjust dynamically via `calculateCardsPerRow()` using `BROWSE_MIN_CARD_WIDTH`
- Logo scales to 50% width on screens narrower than `SMALLER_DEVICES_WIDTH` (380dp)
- `ScreenSizeInfo` expect/actual provides platform-specific screen dimensions

---

## 5. Asset Management

- **Network images:** Loaded via Coil3 through `NetworkImage` composable. Base URL: `https://image.tmdb.org/t/p/w500/` (defined in `Constants.BASE_500_IMAGE_URL`)
- **Local assets:** Compose Multiplatform resources in `composeResources/drawable/` (XML vectors, WebP)
- **Animation:** Lottie JSON in `composeResources/files/erroranimation.json`
- **String resources:** `composeResources/values/strings.xml` with locale variants (es-rES, es-rMX, pt)

---

## 6. Project Structure for UI

```
composeApp/src/commonMain/kotlin/
  common/
    ui/
      theme/          -- Color.kt, Type.kt, Shape.kt, Theme.kt
      components/     -- All reusable composables (23 files)
        button/       -- GenericButton, SimpleButton, SortIconButton
        card/         -- DefaultContentCard, ImageContentCard, MediaTypeTag, PersonImages
        bottomsheet/  -- GenericBottomSheet, RatingBottomSheet, SortBottomSheet, ModalComponents
        popup/        -- GenericPopupMenu, PopupMenuItem, Snackbar
        tab/          -- GenericTabComponents, TabItem
      screen/         -- ErrorScreen, GenericErrorScreen
    util/
      UiConstants.kt  -- All spacing/sizing constants
      CardsUtil.kt    -- Card layout calculations
  features/
    <feature>/
      ui/
        components/   -- Feature-specific composables
```

---

## 7. Figma-to-Code Mapping Rules

When translating Figma designs to this codebase:

1. **Match colors by hex value**, not by Material3 role name. The color scheme mapping is non-standard (e.g., `primary` = black, `secondary` = yellow).
2. **Match typography by font size and weight**, not by Material3 style name. The type scale has known hierarchy inversions (`bodyLarge` > `displaySmall`).
3. **Reuse existing components** before creating new ones. Check the component library in section 2.
4. **Use `UiConstants`** for all spacing and sizing values. Do not introduce magic numbers.
5. **Use `NetworkImage`** for all remote images. Never use Coil directly.
6. **Cards always use** `MainBarGreyColor` background with `BROWSE_CARD_DEFAULT_ELEVATION` (2dp) elevation.
7. **Buttons use** `MaterialTheme.colorScheme.secondary` (yellow) for container, `.primary` (black) for text.
8. **All corners** use `CARD_ROUND_CORNER` (4dp) as the base unit, accessed via `RoundCornerShapes`.
9. **Poster images** always maintain 2:3 aspect ratio (`POSTER_ASPECT_RATIO`).
10. **Icons** must be added as XML vector drawables in `composeResources/drawable/` with `ic_` prefix.

---

## 8. Improvement Recommendations

These are identified issues and suggested improvements. None are implemented yet — they are advisory for future work.

### Visual Polish
- **Color contrast audit:** Verify all text/background combinations meet WCAG AA (4.5:1 for normal text, 3:1 for large text). Key pairs to check: `SecondaryGreyColor` (#9D9D9D) on `PrimaryBlackColor` (#000000), and `PrimaryGreyColor` (#DCDCDC) on `MainBarGreyColor` (#191A1D).
- **Placeholder differentiation:** `placeholderGrey` (#B8B5B5) and `placeholderGrey2` (#8F8B8B) are close in value. Consider increasing contrast between gradient endpoints for a more visible shimmer effect.
- **Yellow accent variants:** The system relies on a single yellow (`#FA9F26`) plus one opacity variant. Consider adding light/dark yellow variants for hover, pressed, and disabled states.

### Structural
- **Semantic token naming:** Rename color tokens to describe purpose, not color:
  - `MainBarGreyColor` → `SurfaceContainer` (nav bars, cards, sheets)
  - `SecondaryGreyColor` → `TextMuted` (secondary text)
  - `PrimaryGreyColor` → `TextTertiary` (unselected labels)
  - `placeholderGrey`/`placeholderGrey2` → `ShimmerStart`/`ShimmerEnd`
  - `DividerGrey` → `Divider`
- **Missing state tokens:** No tokens exist for disabled, hover/pressed, or focus states. This limits component expressiveness on desktop/iPadOS.
- **Typography hierarchy defect:** `bodyLarge` (24sp) is larger than `displaySmall` (23sp) and `headlineSmall` (16sp). The entire body/display/headline progression should be reviewed and realigned to Material3 conventions.

### Component-Level
- **Document component states:** Components lack explicit disabled, loading, and error state variants. For example, `GenericButton` has an `enabled` param but no visual disabled style is documented.
- **Missing common components:** Consider adding: Chip (for filters/tags), Badge (for notification counts), Tooltip (for icon-only actions).
- **Standardize token consumption:** Some components use `MaterialTheme.colorScheme.secondary` while others import `PrimaryYellowColor` directly. Pick one approach and apply consistently. Recommended: use `MaterialTheme` for colors in the scheme, direct imports only for tokens not in the scheme (e.g., `MainBarGreyColor`).
