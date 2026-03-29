# Enhance Action

## Purpose

Provide a right-click context menu entry that triggers AI-powered text enhancement on selected text in the editor.

## Requirements

- The plugin SHALL register an action named "AI Text Polisher: Enhance" in the editor's right-click context menu (`EditorPopupMenu`).
- The action SHALL only be visible and enabled when the user has selected text in the editor.
- The action SHALL validate that the selected text length does not exceed `maxSelectionLength` (default: 2000 characters).
- When selection exceeds the limit, the plugin SHALL display a warning notification and abort the operation.
- The action SHALL execute the AI call on a background thread, never blocking the EDT.
- The action SHALL show a progress indicator while the AI request is in flight.

## Scenarios

### Scenario: User enhances selected text
- GIVEN the user has selected text in an editor
- WHEN they right-click and choose "AI Text Polisher: Enhance"
- THEN the plugin reads the current settings, calls the configured AI provider, and shows the result via the configured display method

### Scenario: No text selected
- GIVEN the user has no text selected
- WHEN they right-click in the editor
- THEN the "AI Text Polisher: Enhance" menu item is not visible

### Scenario: Selection too long
- GIVEN the user has selected text exceeding `maxSelectionLength`
- WHEN they trigger the enhance action
- THEN a warning notification is shown with the character limit
- AND the operation is aborted without calling the AI
