# API Key Security

## Purpose

Securely store and retrieve the AI provider API key using the OS-level credential manager, never exposing it in plaintext configuration files.

## Requirements

- The plugin SHALL store the API key using IntelliJ's `PasswordSafe` API.
- The API key SHALL NOT appear in any XML configuration file, settings export, or log output.
- `PasswordSafe` operations SHALL be executed on background threads, never on the EDT.
- The credential SHALL be identified by service name `AITextPolisher` and account name `apiKey`.
- The settings UI SHALL only write to `PasswordSafe` when the API key field has actually been modified.
- When no API key is configured and the user triggers an enhance action, the plugin SHALL show an error notification directing them to the settings page.

## Scenarios

### Scenario: API key stored securely
- GIVEN the user enters an API key in the settings UI
- WHEN they click Apply
- THEN the key is stored in PasswordSafe (macOS Keychain / Windows Credential Manager / Linux keyring)
- AND the key is NOT written to any XML or JSON file

### Scenario: API key retrieved for request
- GIVEN the user has previously stored an API key
- WHEN the enhance action calls the AI provider
- THEN the key is retrieved from PasswordSafe on a background thread

### Scenario: Missing API key
- GIVEN no API key has been configured
- WHEN the user triggers the enhance action
- THEN an error notification is shown: "API key not configured. Please set it in Settings > Tools > AI Text Polisher."
