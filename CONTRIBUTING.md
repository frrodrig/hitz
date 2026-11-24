# Contributing

Coding agents (Claude Code, Codex, Copilot, Cursor…) read [`AGENTS.md`](AGENTS.md), which also
works as a quick start for people: rules, repository map, commands and conventions.

## Branches and pull requests

- `main` is protected: every change goes through a PR with green checks.
- Branch names: `feat/…`, `fix/…`, `content/…`, `docs/…`, `ci/…`, `build/…`, `test/…`.
- PRs are squash-merged, so **the PR title becomes the commit message** and must follow
  [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). The *PR title*
  workflow checks it.

Allowed types: `feat`, `fix`, `docs`, `content` (course content), `style`, `refactor`, `perf`,
`test`, `build`, `ci`, `chore`, `revert`. The scope is usually the module:
`feat(lesson): match-pairs exercise`.

Code, comments, documentation and commit messages are written in **English**. User-facing
strings live in `values/` (Spanish, the default) and `values-eu/` (Basque).

## Releases

1. Make sure `main` is green and the README screenshots are current
   (`python3 tools/readme_images.py`).
2. Tag `main` with `vMAJOR.MINOR.PATCH` and push the tag: `git tag v0.1.0 && git push origin v0.1.0`
   (or, without git push, create the tag with the API:
   `gh api repos/frrodrig/hitz/git/refs -f ref=refs/tags/v0.1.0 -f sha=$(git rev-parse origin/main)`).
3. The **Release** workflow (`release.yml`) runs three jobs:
   - **build**: the signed AAB and APK, with `versionName` from the tag and
     `versionCode = MAJOR × 10000 + MINOR × 100 + PATCH`. It is the only job that sees the
     keystore, and it deletes it right after building and checks the APK signature.
   - **smoke**: installs the signed, minified APK on an emulator, launches it and runs a seeded
     3 000-event monkey session (`.github/scripts/smoke-test.sh`). A crash or ANR, for example
     from a missing R8 rule, stops the release.
   - **publish**: a GitHub Release with the APK, the AAB, the R8 `mapping.txt` (to read crash
     stack traces) and a changelog grouped by Conventional Commit type since the previous tag
     (`.github/scripts/changelog.py`). `0.x` versions are published as pre-releases.

To check a release without publishing it, run **Release** by hand (Actions → Release → Run
workflow, or `gh workflow run release.yml --ref <branch>`): it builds, signs and smoke-tests, and
skips **publish**.

### Required secrets (Settings → Secrets and variables → Actions)

| Secret | Value |
|---|---|
| `HITZ_KEYSTORE_BASE64` | The keystore encoded in base64 (Linux `base64 -w0 release.jks`, macOS `base64 -i release.jks`) |
| `HITZ_KEYSTORE_PASSWORD` | Keystore password |
| `HITZ_KEY_ALIAS` | Key alias |
| `HITZ_KEY_PASSWORD` | Key password |

Keystores are never committed (`*.jks` and `*.keystore` are git-ignored).

## Screenshot tests

`ui-tests.yml` verifies the Roborazzi screenshots every night. When a UI change is intentional,
run the workflow manually on your branch with **record_screenshots** enabled: it re-records the
references and opens a PR with them against your branch. Review the images, merge that PR, then
yours. The README shows a selection of these references; refresh it with
`python3 tools/readme_images.py`.

## Course audio

The course audio is synthesised with Piper and the HiTZ – Aholab Basque voices. When a branch
changes the course (`content/`, the course assets) or the voice generator, the **Voices** workflow
regenerates only the clips that changed and opens a PR with them against that branch; CI's
*Course audio up to date* step fails until it is merged. See
[`docs/content-guide.md`](docs/content-guide.md#audio).

## Content

Read [`docs/content-guide.md`](docs/content-guide.md) before writing lessons. Never copy
texts, dialogues, exercises or images from the reference textbook.
