#!/usr/bin/env python3
"""Imports voice recordings into the app's audio clips.

Record each line of content/audio/pending.csv and name the file after its clip id
(``u01-1.l1.s1.e03.wav``; any format ffmpeg reads: wav, flac, m4a, mp3, ogg). Then:

    python3 tools/audio/import_recordings.py ~/recordings            # import
    python3 tools/audio/import_recordings.py ~/recordings --dry-run  # only check names
    python3 tools/validate_content.py --write                       # refresh pending.csv

Each file is trimmed of leading/trailing silence, loudness-normalised (−18 LUFS) and encoded as
mono Opus (32 kb/s) into core/data/src/main/assets/audio/<clip id>.ogg. Files whose name is not a
known clip id are reported and skipped. Needs ffmpeg with libopus.
"""
from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from validate_content import DEFAULT_ASSETS, DEFAULT_CONTENT, validate  # noqa: E402

AUDIO_EXTENSIONS = {".wav", ".flac", ".m4a", ".mp3", ".ogg", ".opus", ".aac"}
FILTERS = ",".join([
    "silenceremove=start_periods=1:start_threshold=-45dB:start_silence=0.08",
    "areverse",
    "silenceremove=start_periods=1:start_threshold=-45dB:start_silence=0.15",
    "areverse",
    "loudnorm=I=-18:TP=-2:LRA=7",
])


def known_clip_ids(assets: Path, content: Path) -> set[str]:
    """Clips still pending plus the ones already recorded (so they can be re-recorded)."""
    _, pending = validate(assets, content)
    recorded = {path.stem for path in (assets / "audio").glob("*.ogg")}
    return {clip_id for clip_id, _, _ in pending} | recorded


def encode(source: Path, target: Path) -> None:
    subprocess.run(
        ["ffmpeg", "-y", "-loglevel", "error", "-i", str(source), "-af", FILTERS, "-ac", "1", "-ar", "48000",
         "-c:a", "libopus", "-b:a", "32k", "-map_metadata", "-1", str(target)],
        check=True,
    )


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.split("\n\n")[0])
    parser.add_argument("source", type=Path, help="folder with recordings named <clip id>.<ext>")
    parser.add_argument("--dry-run", action="store_true", help="only check the file names")
    parser.add_argument("--assets", type=Path, default=DEFAULT_ASSETS)
    parser.add_argument("--content", type=Path, default=DEFAULT_CONTENT)
    args = parser.parse_args(argv)

    known = known_clip_ids(args.assets, args.content)
    files = sorted(p for p in args.source.iterdir() if p.suffix.lower() in AUDIO_EXTENSIONS)
    unknown = [p.name for p in files if p.stem not in known]
    for name in unknown:
        print(f"SKIP    {name}: not a clip id of content/audio/pending.csv")
    accepted = [p for p in files if p.stem in known]
    if not args.dry_run:
        (args.assets / "audio").mkdir(parents=True, exist_ok=True)
        for path in accepted:
            encode(path, args.assets / "audio" / f"{path.stem}.ogg")
    action = "would import" if args.dry_run else "imported"
    print(f"{len(accepted)} clip(s) {action}, {len(unknown)} skipped")
    if accepted and not args.dry_run:
        print("Now run: python3 tools/validate_content.py --write")
    return 1 if unknown else 0


if __name__ == "__main__":
    sys.exit(main())
