#!/usr/bin/env python3
"""Synthesises the app's sound effects (original, no samples) into core/data/src/main/res/raw.

Soft sine tones with harmonics and a fast attack / exponential decay; the right-answer sound is a
two-note marimba, quieter than the others. Needs numpy and ffmpeg (libopus).
Run: python3 tools/audio/make_sound_effects.py
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


def marimba(freq: float, seconds: float) -> np.ndarray:
    """A wooden bar: the fundamental plus the bright 4th and 10th partials, which die out fast."""
    t = np.arange(int(RATE * seconds)) / RATE

    def partial(ratio: float, amp: float, attack: float, decay: float) -> np.ndarray:
        return amp * np.sin(2 * np.pi * freq * ratio * t) * np.minimum(1.0, t / attack) * np.exp(-decay * t)

    return partial(1, 1.0, 0.002, 7) + partial(4, 0.25, 0.001, 40) + partial(10, 0.05, 0.001, 90)


def mix(parts: list[tuple[float, np.ndarray]], seconds: float, peak: float) -> np.ndarray:
    """parts: (start seconds, samples); normalised to [peak] with a 10 ms fade-out."""
    out = np.zeros(int(RATE * seconds))
    for start, sound in parts:
        begin = int(RATE * start)
        out[begin:begin + len(sound)] += sound[: len(out) - begin]
    out = out / np.max(np.abs(out)) * peak
    fade = int(0.01 * RATE)
    out[-fade:] *= np.linspace(1.0, 0.0, fade)
    return out


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
    # Marimba, rising fourth: G5 → C6.
    "correct": mix([(0.0, marimba(783.99, 0.6)), (0.085, marimba(1046.5, 0.7))], seconds=0.8, peak=0.6),
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
