# AI Text Polisher

AI-powered text polisher for JetBrains IDEs — select text, press ⌘⇧D, done.

[![JetBrains Marketplace](https://img.shields.io/jetbrains/plugin/v/dev.dalex.textpolisher?label=Marketplace)](https://plugins.jetbrains.com/plugin/dev.dalex.textpolisher)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

---

## What It Does

Stop switching to ChatGPT to fix a sentence. **AI Text Polisher** runs inside your IDE — select any text, trigger the action, and get AI-refined text back in seconds without leaving your editor.

Works anywhere you write: commit messages, docstrings, README files, inline comments, changelogs, API descriptions, or any other text in any file type.

## Features

- **7 AI providers** — Anthropic, OpenAI, DeepSeek, Groq, Mistral, Gemini, Ollama
- **4 enhancement modes** — correct-only, rephrase, formal, concise
- **10 target languages** — English, Chinese, French, German, Spanish, Japanese, Korean, Portuguese, Russian, Italian
- **Custom instructions** — guide the AI with your own prompt (e.g. "Use British English")
- **Inline popup** — review result right below your selection; Apply, View Diff, or Dismiss
- **Diff view** — side-by-side comparison with Apply/Dismiss built in
- **Auto-apply** — skip preview and replace immediately
- **Per-provider config** — each provider remembers its own endpoint, model, and API key
- **Secure key storage** — API keys stored in OS credential manager, never in plain text

## Installation

**From JetBrains Marketplace:**

1. Open **Settings → Plugins → Marketplace**
2. Search for **AI Text Polisher**
3. Click Install and restart the IDE

**From disk:**

1. Download the latest `.zip` from [Releases](https://github.com/dalexhu/dalex-text-polisher/releases)
2. Open **Settings → Plugins → ⚙ → Install Plugin from Disk**
3. Select the downloaded file and restart

## Getting Started

1. Open **Settings → Tools → AI Text Polisher**
2. Choose a provider and enter your API key — the model field auto-fills with a recommended default
3. Select any text in the editor
4. Press **⌘⇧D** (macOS) or **⊞⇧D** (Windows), or right-click → **AI Text Polisher: Enhance**
5. Review the result and choose Apply, View Diff, or Dismiss

## Supported Providers

| Provider | Notes |
|---|---|
| **Anthropic** | Claude Haiku, Sonnet, Opus |
| **OpenAI** | GPT-4o, GPT-4o mini |
| **DeepSeek** | deepseek-chat, deepseek-reasoner |
| **Groq** | Llama 3.3, ultra-fast inference |
| **Mistral** | mistral-large, mistral-small |
| **Gemini** | Gemini 2.0 Flash and Pro |
| **Ollama** | Any local model, fully offline |

All OpenAI-compatible endpoints are supported — set a custom Endpoint URL to use any proxy or self-hosted inference server.

## Keyboard Shortcut

Default: **⌘⇧D** (macOS) · **⊞⇧D** (Windows)

To customize: **Settings → Keymap** → search *AI Text Polisher* → assign any shortcut.

## Building from Source

```bash
git clone https://github.com/dalexhu/dalex-text-polisher.git
cd dalex-text-polisher
./gradlew buildPlugin
```

The plugin zip is generated at `build/distributions/`.

To run a sandboxed IDE instance for testing:

```bash
./gradlew runIde
```

## License

Apache-2.0 — see [LICENSE](LICENSE)
