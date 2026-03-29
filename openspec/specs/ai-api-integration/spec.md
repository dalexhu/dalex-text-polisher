# AI API Integration

## Purpose

Provide a unified abstraction for calling multiple AI providers (Anthropic, OpenAI, Ollama) to polish text.

## Requirements

- The plugin SHALL support three AI providers: `anthropic`, `openai`, and `ollama`.
- Each provider SHALL implement a common `AiClient` interface with a `complete(prompt: String, timeoutMs: Long): String` method.
- A factory method SHALL create the appropriate client based on the configured `provider` setting.
- The client SHALL use the configured `apiEndpoint` when non-empty, otherwise fall back to the provider's official URL.
- The client SHALL include the API key from `PasswordSafe` in the authentication header.
- The client SHALL respect the configured `requestTimeout` (default: 15000ms).
- On network timeout, the client SHALL throw a descriptive exception that the action layer can display to the user.
- On API error responses (4xx, 5xx), the client SHALL throw an exception with the error message from the response body.

## Scenarios

### Scenario: Successful Anthropic API call
- GIVEN provider is set to `anthropic` with a valid API key
- WHEN the client calls `complete()` with a prompt
- THEN it sends a POST to `{endpoint}/v1/messages` with the `x-api-key` header
- AND returns the assistant's text content from the response

### Scenario: Custom endpoint for proxy
- GIVEN `apiEndpoint` is set to `https://proxy.example.com`
- WHEN the client is created
- THEN it uses `https://proxy.example.com` as the base URL instead of the official endpoint

### Scenario: Request timeout
- GIVEN `requestTimeout` is 15000ms
- WHEN the AI provider does not respond within 15 seconds
- THEN the client throws a timeout exception with a user-friendly message
