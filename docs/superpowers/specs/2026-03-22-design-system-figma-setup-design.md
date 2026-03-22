# Design System Documentation & Figma Setup

**Date:** 2026-03-22
**Status:** Approved
**Branch:** feat/design-setup

## Goal

Document CineTracker's existing Compose Multiplatform design system as Figma MCP design system rules, create a FigJam visual reference board, and identify improvement opportunities across visual, structural, and component levels.

## Context

CineTracker has a well-structured dark-theme design system implemented in Compose:
- 13 color tokens mapped to Material3's `darkColorScheme` (Color.kt, Theme.kt)
- 14 typography styles using system SansSerif (Type.kt): displayLarge/Medium/Small, headlineLarge/Medium/Small, titleLarge/Medium/Small, bodyLarge/Medium/Small, labelMedium/Small
- 4 shape variants based on 4dp corners (Shape.kt)
- ~40 spacing/sizing constants (UiConstants.kt)
- ~20 reusable composable components in `common/ui/components/` (cards, buttons, bottom sheets, tabs, ratings, popups, placeholders, etc.)

No Figma file exists yet. The system is only defined in code.

**Source of truth:** The code files (Color.kt, Type.kt, Shape.kt, UiConstants.kt) are authoritative. The style guide (`.claude/rules/style.md`) has known discrepancies with the code (e.g., `DELAY_UPDATE_POPUP_TEXT_MS` documented as 500ms but is 100ms in code, `SEARCH_DEBOUNCE_TIME_MS` documented as 300ms but is 1500ms, casing differences in placeholder color names). The Figma rules must be generated from code, not the style guide.

**Known defect:** The typography scale has a hierarchy inversion — `bodyLarge` (24sp) is larger than both `headlineSmall` (16sp) and `displaySmall` (23sp), which breaks the expected Material3 size progression.

## Approach

**Approach 2: Rules + FigJam Diagram** was selected over:
- Approach 1 (rules only) — lacks visual reference
- Approach 3 (rules + full Figma design file) — over-investment for a personal reference tool

## Deliverables

### 1. Design System Rules File

Generated via Figma MCP `create_design_system_rules`. The MCP scans the codebase and produces a rules file encoding:

- **Color tokens** — All 13 colors with hex values, Material3 mappings, and semantic roles
- **Typography scale** — All 14 styles with size, weight, line height, and usage context
- **Spacing system** — The 4/8/12/16/20/24dp scale plus component-specific dimensions
- **Shape system** — 4 corner radius variants (extraSmall through large)
- **Component inventory** — ~20 reusable composable components with props and usage patterns

### 2. FigJam Visual Board

Generated via Figma MCP `generate_diagram`. Sections:

- **Color Palette** — Swatches grouped by category (accent, backgrounds, text, utility) with hex values and semantic roles
- **Typography Scale** — All styles as sample text with visual hierarchy ladder
- **Spacing & Sizing** — Labeled scale blocks + key component dimensions (poster ratio, card corners, nav bar height)
- **Component Inventory** — Components listed by category (layout, cards, buttons, sheets, navigation, feedback) with purpose and key props
- **Shape System** — Visual examples of 4 shape variants

### 3. Improvement Recommendations

Documented as part of the rules, not implemented in code. Areas:

**Visual polish:**
- Color contrast audit (WCAG AA compliance)
- PlaceholderGrey/DividerGrey differentiation review
- Whether yellow accent needs light/dark variants

**Structural:**
- Semantic token renaming (e.g., MainBarGreyColor -> SurfaceContainer, SecondaryGreyColor -> TextMuted)
- Missing state tokens (disabled, hover/pressed, focus)
- Typography hierarchy fix — `bodyLarge` (24sp) > `displaySmall` (23sp) > `headlineSmall` (16sp), a known defect breaking the Material3 size hierarchy

**Component-level:**
- Document component variants and states (default, selected, disabled, loading)
- Identify missing common components (chip, badge, tooltip)
- Standardize token consumption (MaterialTheme references vs direct color imports)

## Out of Scope

- Refactoring existing code (Color.kt, Type.kt, components, etc.)
- Creating reusable Figma design components
- Code Connect mappings between Figma and code
- Light theme support

## Execution Steps

1. Run `create_design_system_rules` — MCP analyzes codebase, generates rules file
2. Run `generate_diagram` — create FigJam board with all visual sections
3. Review outputs and verify accuracy against current code

## Codebase Impact

- **New files only** — rules file (output location determined by `create_design_system_rules`, typically project root or `.figma/` directory) + this spec
- **No modifications** to existing source files
