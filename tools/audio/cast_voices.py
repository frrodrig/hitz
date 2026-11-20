#!/usr/bin/env python3
"""Basque audio for the course, synthesised offline with Piper.

There are two free Basque Piper voices (HiTZ Basque Center for Language Technology – Aholab,
University of the Basque Country EHU; Apache-2.0, voice data CC BY 4.0): Maider and Antton. The
cast speaks with them as they are: Ane and Maite with Maider, Mikel and Unai with Antton.

Who says each clip:
* dialogue lines (``complete_chat`` messages, story lines) are said by their speaker, with the
  voice that ``content/audio/speakers.json`` gives that name; roles (the shopkeeper, the doctor…)
  take the voice opposite to the person they talk to, so a dialogue never sounds like a monologue;
* a ``complete_chat`` answer is said by the person the last message names, or else by the other
  voice;
* everything else (exercises, key words, guidebook examples) is said by Ane, the guide who
  appears in the prompt bubble.

Each distinct (voice, text) is synthesised once into ``<assets>/audio/tts/<hash>.ogg`` (mono Opus,
24 kb/s, trimmed, −18 LUFS); ``<assets>/audio/tts/index.json`` maps every clip id to its file. A
human recording (``<assets>/audio/<clip id>.ogg``, see tools/audio/import_recordings.py) wins over
the synthetic clip, which is then not generated.

    python3 tools/audio/cast_voices.py download <models dir>
    python3 tools/audio/cast_voices.py generate <models dir>   # (re)generates what changed
    python3 tools/audio/cast_voices.py check                   # CI: is the audio up to date?
"""
from __future__ import annotations

import hashlib
import json
import re
import subprocess
import sys
import tempfile
import urllib.request
import wave
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

TOOLS = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(TOOLS))
from validate_content import DEFAULT_ASSETS, DEFAULT_CONTENT, validate  # noqa: E402

MODELS = {"maider": "itzune/maider-tts", "antton": "itzune/antton-tts"}
CAST = {"ane": "maider", "maite": "maider", "mikel": "antton", "unai": "antton"}
NARRATOR = CAST["ane"]
OPUS = ["-c:a", "libopus", "-b:a", "24k", "-application", "voip", "-ac", "1", "-ar", "48000"]
FILTERS = ",".join([
    "silenceremove=start_periods=1:start_threshold=-50dB",
    "areverse,silenceremove=start_periods=1:start_threshold=-50dB,areverse",
    "loudnorm=I=-18:TP=-1.5:LRA=11",
])
INDEX = "index.json"


# --------------------------------------------------------------------------------------------
# Who says what


def other(voice: str) -> str:
    return "antton" if voice == "maider" else "maider"


class Speakers:
    def __init__(self, content: Path) -> None:
        data = json.loads((content / "audio" / "speakers.json").read_text(encoding="utf-8"))
        self.voices = {name: voice for voice in MODELS for name in data[voice]}
        self.roles = set(data["roles"])

    def check(self, name: str, where: str) -> None:
        if name not in self.voices and name not in self.roles:
            raise SystemExit(f"{where}: speaker '{name}' has no voice in content/audio/speakers.json")

    def dialogue(self, speakers: list[str]) -> dict[str, str]:
        """Voices of everyone in one dialogue; roles take the voice the named people use least."""
        named = [self.voices[s] for s in speakers if s in self.voices]
        role_voice = other(max(MODELS, key=named.count)) if named else NARRATOR
        return {s: self.voices.get(s, role_voice) for s in speakers}


def clip_voices(assets: Path, content: Path) -> dict[str, str]:
    """Voice of every dialogue clip id; clips not listed here are said by the narrator."""
    speakers = Speakers(content)
    voices: dict[str, str] = {}
    for path in sorted((assets / "courses" / "eu-es" / "units").glob("*.json")):
        unit = json.loads(path.read_text(encoding="utf-8"))
        for level in unit["levels"]:
            if level["type"] == "story":
                story = level["story"]
                names = [line["speaker"] for line in story["lines"]]
                for name in names:
                    speakers.check(name, story["id"])
                cast = speakers.dialogue(names)
                for i, name in enumerate(names):
                    voices[f"{story['id']}.{i}"] = cast[name]
                continue
            for lesson in level.get("lessons", []):
                for exercise in lesson["exercises"]:
                    if exercise["type"] != "complete_chat":
                        continue
                    messages = exercise["messages"]
                    names = [m["speaker"] for m in messages]
                    for name in names:
                        speakers.check(name, exercise["id"])
                    cast = speakers.dialogue(names)
                    for i, name in enumerate(names):
                        voices[f"{exercise['id']}.{i}"] = cast[name]
                    voices[f"{exercise['id']}.answer"] = answer_voice(messages[-1], speakers, cast)
    return voices


def answer_voice(last: dict, speakers: Speakers, cast: dict[str, str]) -> str:
    """The reply comes from the person the last message names ("Egun on, Iker!"), else the other voice."""
    words = set(re.findall(r"\w+", last["text"]))
    addressed = [n for n in speakers.voices if n in words and n != last["speaker"]]
    return speakers.voices[addressed[0]] if addressed else other(cast[last["speaker"]])


def clip_file(voice: str, text: str) -> str:
    return hashlib.sha1(f"{voice}\n{text.strip()}".encode()).hexdigest()[:12]


def wanted(assets: Path, content: Path) -> dict[str, tuple[str, str]]:
    """clip id → (voice, text) for every clip without a human recording."""
    report, pending = validate(assets, content)
    if report.errors:
        raise SystemExit(f"fix the content first: {len(report.errors)} validation error(s)")
    voices = clip_voices(assets, content)
    return {clip: (voices.get(clip, NARRATOR), text.strip()) for clip, text, _ in pending}


def expected_index(clips: dict[str, tuple[str, str]]) -> dict[str, str]:
    return {clip: clip_file(voice, text) for clip, (voice, text) in sorted(clips.items())}


# --------------------------------------------------------------------------------------------
# Synthesis


def download(models: Path) -> None:
    models.mkdir(parents=True, exist_ok=True)
    for name, repo in MODELS.items():
        with urllib.request.urlopen(f"https://huggingface.co/api/models/{repo}") as response:
            files = [s["rfilename"] for s in json.load(response)["siblings"]]
        onnx = next(f for f in files if f.endswith(".onnx"))
        for remote, local in ((onnx, f"{name}.onnx"), (onnx + ".json", f"{name}.onnx.json")):
            print(f"{repo}/{remote} -> {local}")
            urllib.request.urlretrieve(f"https://huggingface.co/{repo}/resolve/main/{remote}", models / local)


def encode(wav: Path, target: Path) -> None:
    subprocess.run(
        ["ffmpeg", "-y", "-loglevel", "error", "-i", str(wav), "-af", FILTERS, *OPUS, str(target)],
        check=True,
    )
    wav.unlink()


def generate(models: Path, assets: Path = DEFAULT_ASSETS, content: Path = DEFAULT_CONTENT) -> None:
    from piper import PiperVoice  # only needed here: `check` runs without Piper

    out = assets / "audio" / "tts"
    out.mkdir(parents=True, exist_ok=True)
    clips = wanted(assets, content)
    index = expected_index(clips)
    todo = {index[c]: clips[c] for c in index if not (out / f"{index[c]}.ogg").exists()}
    stale = [p for p in out.glob("*.ogg") if p.stem not in set(index.values())]
    for path in stale:
        path.unlink()
    print(f"{len(index)} clips, {len(set(index.values()))} files: {len(todo)} to synthesise, {len(stale)} removed")

    voices = {name: PiperVoice.load(str(models / f"{name}.onnx")) for name in MODELS}
    with tempfile.TemporaryDirectory() as tmp, ThreadPoolExecutor(max_workers=4) as encoders:
        jobs = []
        for n, (file, (voice, text)) in enumerate(sorted(todo.items()), 1):
            wav = Path(tmp) / f"{file}.wav"
            with wave.open(str(wav), "wb") as w:
                voices[voice].synthesize_wav(text, w)
            jobs.append(encoders.submit(encode, wav, out / f"{file}.ogg"))
            if n % 250 == 0:
                print(f"  {n}/{len(todo)}", flush=True)
        for job in jobs:
            job.result()
    (out / INDEX).write_text(json.dumps(index, ensure_ascii=False, indent=0) + "\n", encoding="utf-8")


def check(assets: Path = DEFAULT_ASSETS, content: Path = DEFAULT_CONTENT) -> int:
    """0 when every clip has its synthetic file and the index is current."""
    out = assets / "audio" / "tts"
    index = expected_index(wanted(assets, content))
    current = json.loads((out / INDEX).read_text(encoding="utf-8")) if (out / INDEX).exists() else {}
    missing = sorted({f for f in index.values() if not (out / f"{f}.ogg").exists()})
    if current == index and not missing:
        print(f"audio up to date: {len(index)} clips, {len(set(index.values()))} files")
        return 0
    changed = sum(1 for c in index if current.get(c) != index[c]) + len(set(current) - set(index))
    print(
        f"audio out of date: {changed} clip(s) changed, {len(missing)} file(s) missing. "
        "Run the Voices workflow on this branch (Actions → Voices → Run workflow) to regenerate it.",
    )
    return 1


if __name__ == "__main__":
    command, *args = sys.argv[1:]
    if command == "download":
        download(Path(args[0]))
    elif command == "generate":
        generate(Path(args[0]))
    elif command == "check":
        sys.exit(check())
    else:
        sys.exit(f"unknown command {command}")
