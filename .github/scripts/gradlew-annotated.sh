#!/usr/bin/env bash
# Ejecuta ./gradlew y, si falla, publica los errores como anotaciones del check
# (bloque "What went wrong", errores del compilador "e: …" y fallos de tests),
# para poder leerlos en la pestaña de checks del PR o a través de la API.
set -uo pipefail

log="$(mktemp)"
./gradlew "$@" 2>&1 | tee "$log"
status=${PIPESTATUS[0]}

if [ "$status" -ne 0 ]; then
  encode() { sed -e 's/%/%25/g' -e 's/\r//g' | awk 'BEGIN{ORS="%0A"} {print}'; }

  # Errores de compilación de Kotlin, de Lint y de tests (máx. 40 líneas).
  grep -E '^(e: |ERROR:|FAILURE:|.*FAILED$|.* > .* FAILED)|Error: |error: ' "$log" | head -n 40 > "$log.err" || true
  if [ -s "$log.err" ]; then
    echo "::error title=Errores de Gradle ($*)::$(encode < "$log.err")"
  fi

  # Bloque "What went wrong" completo (configuración, dependencias, etc.).
  awk '/\* What went wrong:/{f=1} /\* Try:/{f=0} f' "$log" | head -n 60 > "$log.wrong" || true
  if [ -s "$log.wrong" ]; then
    echo "::error title=What went wrong ($*)::$(encode < "$log.wrong")"
  fi
fi

exit "$status"
