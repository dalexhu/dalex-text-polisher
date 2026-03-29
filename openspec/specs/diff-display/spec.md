# Diff Display

## Purpose

Show the AI-polished result alongside the original text and let the user decide whether to apply the change.

## Requirements

- The plugin SHALL display a diff view using IntelliJ's built-in `DiffManager` with a `SimpleDiffRequest`.
- The diff view SHALL show the original text on the left and the polished text on the right.
- After the diff dialog closes, the plugin SHALL ask the user to confirm replacement via a Yes/No dialog.
- On confirmation, the plugin SHALL replace the selected text using `WriteCommandAction`.
- When `autoApply` is enabled, the plugin SHALL skip the diff view and directly replace the text.
- Text replacement SHALL preserve the original selection range and cursor position.

## Scenarios

### Scenario: User reviews and applies
- GIVEN the AI returns polished text
- WHEN the diff view is shown
- AND the user clicks "Apply" (Yes)
- THEN the selected text in the editor is replaced with the polished text

### Scenario: User dismisses
- GIVEN the AI returns polished text
- WHEN the diff view is shown
- AND the user clicks "Dismiss" (No)
- THEN the original text remains unchanged

### Scenario: Auto-apply enabled
- GIVEN `autoApply` is `true`
- WHEN the AI returns polished text
- THEN the selected text is replaced immediately without showing a diff
