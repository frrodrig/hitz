#!/usr/bin/env python3
"""Synthesises the app's sound effects (original, no samples) into core/data/src/main/res/raw.

Soft sine tones with harmonics and a fast attack / exponential decay. Needs numpy and ffmpeg
(libopus). Run: python3 tools/audio/make_sound_effects.py
"""
from __future__ import annotations

import subprocess
import tempfile
import wave
from pathlib import Path

import numpy as np

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "core" / "data" / "src" / "main" / "res" / "raw"
RATE = 48_000


def tone(freq: float, seconds: float, decay: float = 6.0, harmonics: tuple[float, ...] = (1.0, 0.35, 0.12)) -> np.ndarray:
    t = np.arange(int(RATE * seconds)) / RATE
    wave_ = sum(amp * np.sin(2 * np.pi * freq * (n + 1) * t) for n, amp in enumerate(harmonics))
    attack = np.minimum(1.0, t / 0.008)
    return wave_ * attack * np.exp(-decay * t)


def sequence(notes: list[tuple[float, float, float]], tail: float = 0.4) -> np.ndarray:
    """notes: (start seconds, frequency, length seconds)."""
    total = max(start + length for start, _, length in notes) + tail
    out = np.zeros(int(RATE * total))
    for start, freq, length in notes:
        sound = tone(freq, length + tail)
        begin = int(RATE * start)
        out[begin:begin + len(sound)] += sound[: len(out) - begin]
    return out / np.max(np.abs(out)) * 0.8


EFFECTS = {
    # Rising major sixth: E5 → C#6.
    "correct": sequence([(0.0, 659.25, 0.12), (0.09, 1108.73, 0.25)]),
    # Soft falling minor second, low: A3 → G#3.
    "incorrect": sequence([(0.0, 220.0, 0.14), (0.12, 207.65, 0.3)], tail=0.2),
    # Arpeggio C5 E5 G5 C6.
    "finished": sequence([(0.0, 523.25, 0.1), (0.1, 659.25, 0.1), (0.2, 783.99, 0.1), (0.3, 1046.5, 0.5)], tail=0.6),
}


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        for name, samples in EFFECTS.items():
            wav_path = Path(tmp) / f"{name}.wav"
            with wave.open(str(wav_path), "wb") as wav:
                wav.setnchannels(1)
                wav.setsampwidth(2)
                wav.setframerate(RATE)
                wav.writeframes((samples * 32767).astype(np.int16).tobytes())
            target = OUT / f"core_data_sfx_{name}.ogg"
            subprocess.run(
                ["ffmpeg", "-y", "-loglevel", "error", "-i", str(wav_path), "-c:a", "libopus", "-b:a", "48k",
                 "-map_metadata", "-1", "-fflags", "+bitexact", "-flags:a", "+bitexact", str(target)],
                check=True,
            )
            print(f"{target.relative_to(ROOT)}: {target.stat().st_size} bytes")


if __name__ == "__main__":
    main()
