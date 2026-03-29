# Prompt Construction

## Purpose

Build the AI prompt from the selected text, enhancement mode, target language, and optional custom instructions.

## Requirements

- The prompt builder SHALL construct a system message defining the AI's role as a text polisher.
- The prompt builder SHALL include mode-specific instructions:
  - `correct-only`: Fix only spelling and grammar errors, do not change style or wording.
  - `rephrase`: Rephrase for natural fluency while preserving the original meaning.
  - `formal`: Rewrite in formal, professional language.
  - `concise`: Make the text more concise while keeping the meaning intact.
- The prompt builder SHALL specify the target language (e.g., "Output in English").
- The prompt builder SHALL append the user's `customPrompt` if non-empty.
- The prompt builder SHALL instruct the AI to return ONLY the polished text with no explanations, preamble, or markdown formatting.
- The selected text SHALL be passed as the user message content.

## Scenarios

### Scenario: Correct-only mode in English
- GIVEN mode is `correct-only` and target language is `English`
- WHEN the prompt is built with selected text "Ths is a tset"
- THEN the system prompt includes instructions to fix only spelling/grammar
- AND specifies English as the output language
- AND the user message contains "Ths is a tset"

### Scenario: Custom prompt appended
- GIVEN mode is `rephrase` and customPrompt is "Use British English"
- WHEN the prompt is built
- THEN the system prompt includes rephrase instructions followed by "Use British English"
