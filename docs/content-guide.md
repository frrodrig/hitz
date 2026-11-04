# Guía de contenido

> Este documento se completa en M2 (modelo de contenido y validador). Por ahora recoge las
> reglas que ya están en vigor.

## Principios

1. **Contenido original.** El libro *Arian A1* solo marca el temario (orden, objetivos, gramática
   y campos léxicos). No se copian textos, diálogos, ejercicios, listas ni imágenes.
2. **Euskera batua.** Cualquier duda se marca con `TODO(revisión-nativa)` en el JSON (campo
   `notes`) o en un comentario, y no se publica sin revisar.
3. **Solo vocabulario del temario.** Cada lección usa palabras de su unidad o de unidades
   anteriores según `content/syllabus/vocabulary.csv`. El validador lo comprobará (M2).

## Temario

- Fuente de datos: `content/syllabus/vocabulary.csv`, con las columnas `lemma`, `es`, `pos`,
  `section`, `book_unit`, `app_unit`, `set` (`core` o `closed`) y `note`.
- `docs/syllabus.md` se regenera con `python3 tools/syllabus_vocab.py`, y el CI comprueba que
  esté sincronizado (`--check`) y que cada sección tenga entre 100 y 150 palabras núcleo.
- Las series cerradas (números, días de la semana y meses) usan `set=closed` y no cuentan en
  ese límite.

## Estructura (prevista para M2)

```
app/src/main/assets/courses/eu-es/   JSON del curso (Course > Section > Unit > Level > Lesson > Exercise)
app/src/main/assets/audio/           Audios pregrabados, nombrados por id
content/schema/                      JSON Schema del contenido
content/syllabus/                    Temario como datos
tools/                               Validadores y generadores
```
