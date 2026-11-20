#!/usr/bin/env python3
"""Basque voices for the cast, synthesised offline with Piper.

There are two free Basque Piper voices (HiTZ / Aholab, EHU; Apache-2.0, voice data CC BY 4.0):
Maider and Antton. Each cast member speaks with one of them, shaped by a speed (Piper's length
scale) and a pitch shift (semitones, ffmpeg rubberband with formants preserved), so that the two
women and the two men still sound like different people.

    python3 tools/audio/cast_voices.py download <models dir>
    python3 tools/audio/cast_voices.py samples <models dir> <out dir>

``samples`` writes one Ogg Opus clip per (voice, line) plus ``samples.json``, to judge the voices
before generating the course.
"""
from __future__ import annotations

import json
import subprocess
import sys
import tempfile
import urllib.request
from dataclasses import dataclass
from pathlib import Path

MODELS = {
    "maider": "itzune/maider-tts",
    "antton": "itzune/antton-tts",
}


@dataclass(frozen=True)
class Voice:
    model: str
    length_scale: float = 1.0
    semitones: float = 0.0


CAST = {
    "ane": Voice("maider"),                                      # young trikitilari
    "maite": Voice("maider", length_scale=1.12, semitones=-2.0),  # the amona
    "mikel": Voice("antton", length_scale=1.05, semitones=-1.0),  # aizkolari
    "unai": Voice("antton", length_scale=0.95, semitones=1.5),    # young pelotari
}
BASE = {"maider": Voice("maider"), "antton": Voice("antton")}

# Course lines that exercise x, tx, tz, z, s, j and a question; each member also says hello.
SAMPLE_LINES = [
    "kaixo",
    "udaletxe",
    "hondartza",
    "jatetxea",
    "Sei, zazpi, zortzi, bederatzi, hamar.",
    "Poliki, mesedez. Euskaraz pixka bat hitz egiten dut.",
    "Zu euskara ikasten ari zara?",
]
GREETING = "Kaixo! Ni {name} naiz."

OPUS = ["-c:a", "libopus", "-b:a", "32k", "-ac", "1", "-ar", "48000"]


def download(models_dir: Path) -> None:
    models_dir.mkdir(parents=True, exist_ok=True)
    for name, repo in MODELS.items():
        with urllib.request.urlopen(f"https://huggingface.co/api/models/{repo}") as response:
            files = [s["rfilename"] for s in json.load(response)["siblings"]]
        onnx = next(f for f in files if f.endswith(".onnx"))
        for remote, local in ((onnx, f"{name}.onnx"), (onnx + ".json", f"{name}.onnx.json")):
            url = f"https://huggingface.co/{repo}/resolve/main/{remote}"
            print(f"{url} -> {local}")
            urllib.request.urlretrieve(url, models_dir / local)


def synthesize(text: str, voice: Voice, models_dir: Path, target: Path) -> None:
    """Writes [text] spoken by [voice] to [target] (Ogg Opus, trimmed, −18 LUFS)."""
    with tempfile.TemporaryDirectory() as tmp:
        wav = Path(tmp) / "raw.wav"
        subprocess.run(
            [sys.executable, "-m", "piper", "-m", str(models_dir / f"{voice.model}.onnx"),
             "--length-scale", str(voice.length_scale), "-f", str(wav)],
            input=text, text=True, check=True, capture_output=True,
        )
        filters = []
        if voice.semitones:
            filters.append(f"rubberband=pitch={2 ** (voice.semitones / 12):.5f}:formant=preserved")
        filters += [
            "silenceremove=start_periods=1:start_threshold=-50dB",
            "areverse,silenceremove=start_periods=1:start_threshold=-50dB,areverse",
            "loudnorm=I=-18:TP=-1.5:LRA=11",
        ]
        subprocess.run(
            ["ffmpeg", "-y", "-loglevel", "error", "-i", str(wav), "-af", ",".join(filters), *OPUS, str(target)],
            check=True,
        )


def samples(models_dir: Path, out: Path) -> None:
    out.mkdir(parents=True, exist_ok=True)
    index = []
    voices = {**CAST, **{f"base-{name}": voice for name, voice in BASE.items()}}
    for who, voice in voices.items():
        lines = SAMPLE_LINES if who.startswith("base-") else [GREETING.format(name=who.capitalize()), *SAMPLE_LINES]
        for number, line in enumerate(lines):
            name = f"{who}-{number:02d}.ogg"
            synthesize(line, voice, models_dir, out / name)
            index.append({"voice": who, "text": line, "file": name})
            print(name, line)
    (out / "samples.json").write_text(json.dumps(index, ensure_ascii=False, indent=1), encoding="utf-8")


if __name__ == "__main__":
    command, *args = sys.argv[1:]
    if command == "download":
        download(Path(args[0]))
    elif command == "samples":
        samples(Path(args[0]), Path(args[1]))
    else:
        sys.exit(f"unknown command {command}")
