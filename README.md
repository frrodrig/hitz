# Hitz

[![CI](https://github.com/frrodrig/hitz/actions/workflows/ci.yml/badge.svg)](https://github.com/frrodrig/hitz/actions/workflows/ci.yml)

App Android para aprender **euskera desde castellano** (nivel A1), con una ruta de lecciones
cortas, repetición espaciada y gamificación. Su mascota es un **Galtzagorri**, uno de los
duendecillos de calzón rojo de la mitología vasca que no paran de trabajar.

- 100 % offline y sin backend de pago. Las ligas se simulan en local.
- Interfaz en castellano y en euskera.
- Todo el contenido del curso es original. El temario sigue el orden curricular de *Arian A1*
  (ver [`docs/syllabus.md`](docs/syllabus.md)).

## Estado

| Hito | Contenido | Estado |
|---|---|---|
| M0 | Repo, build-logic y CI | 🚧 |
| M1 | Design system y tema | ⏳ |
| M2 | Modelo de contenido y validador | ⏳ |
| M3 | Motor de lección y 4 tipos de ejercicio | ⏳ |
| M4 | Ruta de aprendizaje y progreso | ⏳ |
| M5 | Resto de ejercicios | ⏳ |
| M6 | Gamificación | ⏳ |
| M7 | Contenido completo del A1 | ⏳ |
| M8 | Audio, accesibilidad y pulido | ⏳ |
| M9 | Release | ⏳ |

## Compilar

Requisitos: JDK 17 y el SDK de Android (API 37).

```bash
./gradlew assembleDebug          # APK de depuración
./gradlew detekt                 # estilo (detekt + reglas de ktlint)
./gradlew lintDebug              # Android Lint
./gradlew testDebugUnitTest      # tests unitarios
./gradlew recordRoborazziDebug   # regenerar capturas de referencia
python3 tools/syllabus_vocab.py  # regenerar las tablas del temario
```

Cada PR genera un APK de depuración como *artifact* del workflow **CI**.

## Documentación

- [`docs/architecture.md`](docs/architecture.md): módulos, capas y decisiones técnicas.
- [`docs/content-guide.md`](docs/content-guide.md): cómo se escribe y valida el contenido.
- [`docs/syllabus.md`](docs/syllabus.md): temario (unidades, objetivos, gramática y vocabulario).
- [`CONTRIBUTING.md`](CONTRIBUTING.md): ramas, commits y releases.
