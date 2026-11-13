#!/usr/bin/env bash
# Smoke test of a release APK on a running emulator: install, launch, then a seeded monkey
# session. Fails if the app crashes or stops responding, which is what an R8 rule missing for
# Hilt, Room or kotlinx.serialization would cause. Usage: smoke-test.sh <apk> <log dir>
set -euo pipefail
apk="$1"; logs="$2"; package="eus.hitz.app"
mkdir -p "$logs"

adb install -r "$apk"
adb logcat -c
adb shell am start -W -n "$package/.MainActivity"
sleep 8
adb exec-out screencap -p > "$logs/launch.png"

# 3 000 random events (fixed seed: the same run every time), only inside the app.
adb shell monkey -p "$package" -s 4242 --throttle 120 --pct-syskeys 0 --pct-appswitch 0 \
  --ignore-security-exceptions -v 3000 > "$logs/monkey.txt" 2>&1 || true
adb exec-out screencap -p > "$logs/after-monkey.png"
adb logcat -d > "$logs/logcat.txt"

if grep -q -E "// CRASH|// NOT RESPONDING" "$logs/monkey.txt"; then
  echo "::error::The release app crashed or stopped responding during the monkey session"
  grep -A 30 -E "// CRASH|// NOT RESPONDING" "$logs/monkey.txt" | head -60
  exit 1
fi
if grep -q -E "FATAL EXCEPTION.*|AndroidRuntime: Process: $package" "$logs/logcat.txt"; then
  echo "::error::The release app crashed (see logcat)"
  grep -A 30 "FATAL EXCEPTION" "$logs/logcat.txt" | head -60
  exit 1
fi
grep -E "Events injected" "$logs/monkey.txt" || true
echo "Release APK survived launch and the monkey session"
