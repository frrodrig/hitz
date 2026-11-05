"""Tests for tools/audio/import_recordings.py (skipped without ffmpeg)."""
from __future__ import annotations

import shutil
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "audio"))
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

import import_recordings as ir  # noqa: E402
import validate_content as vc  # noqa: E402


@unittest.skipUnless(shutil.which("ffmpeg"), "ffmpeg is not installed")
class ImportRecordingsTest(unittest.TestCase):
    def setUp(self) -> None:
        self.tmp = Path(tempfile.mkdtemp())
        self.assets = self.tmp / "assets"
        self.content = self.tmp / "content"
        shutil.copytree(vc.DEFAULT_ASSETS, self.assets)
        shutil.copytree(vc.DEFAULT_CONTENT, self.content)
        self.recordings = self.tmp / "recordings"
        self.recordings.mkdir()

    def tearDown(self) -> None:
        shutil.rmtree(self.tmp)

    def record(self, name: str) -> None:
        # Half a second of silence, a tone, and silence again.
        tone = "aevalsrc='if(between(t,0.5,1),0.3*sin(2*PI*220*t),0)':s=44100:d=1.5"
        subprocess.run(
            ["ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i", tone, str(self.recordings / name)],
            check=True,
        )

    def run_import(self, *extra: str) -> int:
        return ir.main([str(self.recordings), "--assets", str(self.assets), "--content", str(self.content), *extra])

    def test_known_clips_are_encoded_and_leave_the_pending_list(self) -> None:
        _, before = vc.validate(self.assets, self.content)
        clip_id = before[0][0]
        self.record(f"{clip_id}.wav")
        self.assertEqual(self.run_import(), 0)
        self.assertTrue((self.assets / "audio" / f"{clip_id}.ogg").exists())
        _, after = vc.validate(self.assets, self.content)
        self.assertEqual(len(after), len(before) - 1)

    def test_unknown_names_are_skipped(self) -> None:
        self.record("not-a-clip.wav")
        self.assertEqual(self.run_import("--dry-run"), 1)
        self.assertFalse((self.assets / "audio" / "not-a-clip.ogg").exists())


if __name__ == "__main__":
    unittest.main()
