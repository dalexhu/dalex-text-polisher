# Settings & Configuration

## Purpose

Provide a persistent, user-friendly settings interface for configuring the plugin's connection, enhancement, and behavior options.

## Requirements

- The plugin SHALL store configuration using `PersistentStateComponent` (persisted to `AITextPolisher.xml`).
- The settings UI SHALL be accessible at Settings > Tools > AI Text Polisher.
- The settings UI SHALL be organized into three groups:
  - **Connection**: provider, API key (password field), endpoint, model
  - **Enhancement**: target language, mode, custom prompt
  - **Behavior**: auto-apply, request timeout, max selection length
- The settings UI SHALL use Kotlin UI DSL 2 (`com.intellij.ui.dsl.builder.panel`).
- The API key field SHALL be a password input (masked characters).
- Changes SHALL only be persisted when the user clicks "Apply" or "OK".

### Configuration Defaults

| Key | Type | Default |
|---|---|---|
| provider | enum | anthropic |
| model | string | claude-haiku-4-5 |
| apiEndpoint | string | "" (empty = official) |
| targetLanguage | enum | English |
| mode | enum | correct-only |
| customPrompt | string | "" |
| autoApply | boolean | false |
| requestTimeout | int | 15000 |
| maxSelectionLength | int | 2000 |

## Scenarios

### Scenario: User configures provider
- GIVEN the user opens Settings > Tools > AI Text Polisher
- WHEN they change provider to `openai` and click Apply
- THEN subsequent enhance actions use the OpenAI client

### Scenario: Settings persist across restarts
- GIVEN the user has configured custom settings
- WHEN they restart the IDE
- THEN all settings are restored to their previously configured values
