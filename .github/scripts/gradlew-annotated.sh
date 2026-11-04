#!/usr/bin/env bash
# Runs ./gradlew and, on failure, publishes the errors as check annotations
# ("What went wrong" block, Kotlin compiler "e: …" lines and failed tests) so they can
# be read from the PR checks tab or through the REST API.
set -uo pipefail

log="$(mktemp)"
./gradlew "$@" 2>&1 | tee "$log"
status=${PIPESTATUS[0]}

if [ "$status" -ne 0 ]; then
  encode() { sed -e 's/%/%25/g' -e 's/\r//g' | awk 'BEGIN{ORS="%0A"} {print}'; }

  # Kotlin compiler, Lint and test errors (at most 60 lines, stack frames removed).
  grep -E -A4 '^(e: |ERROR:|FAILURE:|.*FAILED$)|Error: |error: ' "$log" | grep -v -E '^\s+at ' | head -n 60 > "$log.err" || true
  if [ -s "$log.err" ]; then
    echo "::error title=Gradle errors ($*)::$(encode < "$log.err")"
  fi

  # Full "What went wrong" block (configuration, dependency resolution, ...).
  awk '/\* What went wrong:/{f=1} /\* Try:/{f=0} f' "$log" | head -n 60 > "$log.wrong" || true
  if [ -s "$log.wrong" ]; then
    echo "::error title=What went wrong ($*)::$(encode < "$log.wrong")"
  fi
fi

exit "$status"
