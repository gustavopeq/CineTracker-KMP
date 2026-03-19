# UI Style Guide

## Theme

**Dark theme only** — there is no light theme. All new UI must follow this dark palette.

## Color Palette

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

## Typography

All fonts use `SansSerif` (system default). Key styles used across the app:

- **`headlineLarge`** (20sp, w420) — Top bar titles
- **`headlineMedium`** (18sp) — Bottom sheet headers
- **`titleMedium`** (18sp, w500) — Card titles, button text
- **`titleSmall`** (14sp, w500) — Tags, labels (`MediaTypeTag`)
- **`bodyMedium`** (16sp) — General content text
- **`bodySmall`** (14sp) — Popup menu items
- **`labelSmall`** (12sp) — Nav bar labels, small captions
- **`displayLarge`** (32sp) — Large display text

## Shapes

All shapes use `CARD_ROUND_CORNER = 4dp` as the base unit:
- `extraSmall`: 4dp bottom corners only (cards with top image)
- `small`: 4dp top corners only (image tops)
- `medium`: 6dp all corners
- `large`: 8dp all corners

## Spacing

**Padding:** `SMALL_PADDING = 4dp`, `DEFAULT_PADDING = 8dp`, `LARGE_PADDING = 12dp`
**Margins:** `SMALL_MARGIN = 8dp`, `DEFAULT_MARGIN = 16dp`, `LARGE_MARGIN = 20dp`
**Section gap:** `SECTION_PADDING = 24dp`
**Screen horizontal margin:** 16dp (consistently used across features)

## Component Sizing

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

## Existing Reusable Components

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

## UI Patterns

- **Cards:** `MainBarGreyColor` background + 2dp elevation. Image on top (cropped, 1.5× aspect), text below.
- **Selected state:** Yellow color for icons and text. Unselected uses white or grey.
- **Loading:** Shimmer placeholders for images; centered yellow circular indicator for pages.
- **Empty states:** Centered text with ~30% top padding offset.
- **Details screen:** Full-width poster with scroll-based alpha fade, content offset at 70% of poster height.
- **Home screen:** Featured poster at full width, followed by horizontal carousels (150dp cards).
- **Gradients:** Black-to-transparent overlays on poster images for text readability.
- **Scroll behavior:** Collapsing tab rows in Browse; nested scroll connections for coordinated scrolling.
- **Responsive:** Logo scales to 50% width on screens < 380dp; grid columns adjust dynamically.
