#!/usr/bin/env python3
"""Release notes from Conventional Commit subjects, grouped by type.

    python3 .github/scripts/changelog.py <tag>

Lists the commits since the previous v* tag (or since the beginning, for the first release).
Squash-merged PRs keep their "(#N)" suffix, which GitHub turns into links.
"""
import re
import subprocess
import sys

GROUPS = [
    ("feat", "New features"),
    ("content", "Course content"),
    ("fix", "Fixes"),
    ("perf", "Performance"),
]
OTHER = "Other changes"
DEPENDENCIES = "Dependencies"


def git(*args: str) -> str:
    return subprocess.run(["git", *args], check=True, capture_output=True, text=True).stdout.strip()


def is_ancestor(older: str, newer: str) -> bool:
    return subprocess.run(["git", "merge-base", "--is-ancestor", older, newer]).returncode == 0


def main() -> None:
    tag = sys.argv[1]
    tags = [t for t in git("tag", "--list", "v*", "--sort=-v:refname").splitlines() if t != tag]
    previous = next((t for t in tags if is_ancestor(t, tag)), None)
    span = f"{previous}..{tag}" if previous else tag
    sections: dict[str, list[str]] = {}
    for subject in git("log", "--format=%s", "--no-merges", span).splitlines():
        m = re.match(r"^(\w+)(\([^)]*\))?(!)?:\s*(.+)$", subject)
        kind, text = (m.group(1), m.group(4)) if m else ("", subject)
        if kind == "build" and text.lower().startswith("bump "):
            title = DEPENDENCIES
        else:
            title = dict(GROUPS).get(kind, OTHER)
        sections.setdefault(title, []).append(text[0].upper() + text[1:])
    order = [title for _, title in GROUPS] + [OTHER, DEPENDENCIES]
    print("Hitz, an app to learn Basque (euskera batua, A1.1) from Spanish.\n")
    print("Install `hitz-*.apk` on Android 8.0 or later; `hitz-*.aab` is the bundle for app stores.\n")
    for title in order:
        if title in sections:
            print(f"## {title}\n")
            print("\n".join(f"- {line}" for line in sections[title]))
            print()
    print(f"**Full history:** {previous + '...' + tag if previous else 'every commit up to ' + tag}")


if __name__ == "__main__":
    main()
