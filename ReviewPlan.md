# Review Plan — SharpBasicPlugin (2026-07-03)

Findings from a full cross-project code review (see `../SharpBasicShared/ReviewPlan.md`
for the shared-library side). Overall: the coupling to the shared library is healthy —
the JFlex lexer and completion contributor consume `KeywordRegistry`, and the reformat
actions reuse the shared ANTLR visitors instead of reimplementing them. The plugin
necessarily keeps its own Grammar-Kit PSI parser (IntelliJ requirement); that is the
right boundary. Tests pass (`./gradlew test`). Note: the shared library's docs still say
the plugin should use only `sharp-basic-core` — that statement is stale, not this code.

## P1 — User-facing risk

### 1.1 Reformat actions can silently mangle files with syntax errors
None of the five reformatters checks `parser.getNumberOfSyntaxErrors()`. The actions then
call `document.setText(...)` with output generated from ANTLR's *error-recovered* tree —
reformatting a file containing a typo can silently drop or rewrite code (undo saves the
user, but only if they notice).
**Fix:** check the syntax-error count after parsing; on errors, show the existing
notification balloon ("file has N syntax errors — not reformatted") and leave the
document untouched.

## P2 — Duplication (fold into shared façade when available)

### 2.1 Five copy-pasted reformatter classes
`SharpBasicCodeReformatter`, `SharpBasicNiceReformatter`, `SharpBasicCompactReformatter`,
`SharpBasicStrippedReformatter`, `SharpBasicRenumReformatter` are identical except for
the visitor instantiation; `expandSource`, `detectLineEnding`, `restoreLineEndings` exist
5×. **Fix:** one reformatter parameterized by visitor (or by the shared library's planned
`SharpBasicPipeline` emitters — shared ReviewPlan §2.1 — which would also bring the
error-count check of P1.1 for free).

### 2.2 Five near-identical action classes
The `ReformatAs*Action` classes (~125 lines each) differ only in title strings, scratch
suffix, and which reformatter they call. **Fix:** extract an abstract base action with
`getTitle()/getScratchSuffix()/reformat(String)`.

## P3 — Build & configuration

- **`gradle.properties` hardcodes `org.gradle.java.home`** to
  `/opt/homebrew/opt/openjdk@21/...` — breaks the build on any other machine/CI. Move to
  a local, uncommitted override (`~/.gradle/gradle.properties` or an env toolchain) and
  prefer Gradle Java toolchains.
- **Old test dependencies:** JUnit pinned at 5.10.1 (with vintage engine + JUnit 4.13.2).
  Update to current stable JUnit and check whether the JUnit-4 vintage path is still
  needed.
- **Redundant dependency:** `org.antlr:antlr4-runtime:4.13.2` is declared explicitly but
  already arrives transitively via `sharp-basic-antlr`; drop it so the version can't
  drift from the one shared prescribes.
- **Registry duplication in consumers of PC-1500 only:** reformatters and completion all
  hardcode `KeywordRegistry.forPc1500()`. Fine as a product decision, but when PC-1600
  support arrives, thread a single registry/device setting through instead of touching
  six call sites. (Beware: `forPc1600().allKeywords()` currently returns duplicates —
  shared ReviewPlan §1.4 — which would show up directly in the completion popup.)

## P4 — Housekeeping

- `src/main/gen` (generated lexer/parser/PSI, ~90 files) is committed; regeneration is
  wired into the build (`generateLexer`/`generateParser`). Consider un-committing the
  generated sources to avoid stale-diff noise, or document why they are checked in.
- Root contains overlapping docs (`IMPLEMENTATION_SUMMARY.md`, `PLUGIN_ARCHITECTURE.md`,
  `QUICK_START.md`, `BUILDING.md`, `README.md`) — several describe pre-shared-library
  states of the code. Worth one consolidation pass.
