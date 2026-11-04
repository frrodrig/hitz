# Contributing

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

1. Update `README.md` if the milestone status changed.
2. Tag `main` with `vMAJOR.MINOR.PATCH` and push the tag: `git tag v0.1.0 && git push origin v0.1.0`.
3. The **Release** workflow builds the signed AAB and APK and publishes a GitHub Release with a
   generated changelog.

### Required secrets (Settings → Secrets and variables → Actions)

| Secret | Value |
|---|---|
| `HITZ_KEYSTORE_BASE64` | The keystore encoded in base64 (`base64 -w0 release.jks`) |
| `HITZ_KEYSTORE_PASSWORD` | Keystore password |
| `HITZ_KEY_ALIAS` | Key alias |
| `HITZ_KEY_PASSWORD` | Key password |

Keystores are never committed (`*.jks` and `*.keystore` are git-ignored).

## Screenshot tests

`ui-tests.yml` verifies the Roborazzi screenshots every night. When a UI change is intentional,
run the workflow manually with **record_screenshots** enabled: it re-records the references
and opens a PR with them.

## Content

Read [`docs/content-guide.md`](docs/content-guide.md) before writing lessons. Never copy
texts, dialogues, exercises or images from the reference textbook.
