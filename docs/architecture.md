# Arquitectura

## Módulos

```
:app                       Activity, navegación raíz, Hilt application
:feature:path              Ruta de aprendizaje (nodos, secciones, guía de unidad)
:feature:lesson            Motor de lección y ejercicios
:feature:profile           Perfil, racha, logros
:feature:leaderboard       Ligas simuladas en local
:feature:shop              Tienda de gemas
:core:designsystem         Tema Material 3, componentes y previews
:core:data                 Repositorios, Room, DataStore, assets de contenido
:core:domain               Casos de uso e interfaces de repositorio (Kotlin puro)
:core:model                Modelo de dominio (Kotlin puro)
```

Reglas de dependencia:

- `feature:*` → `core:designsystem`, `core:domain`, `core:model`. Una feature nunca depende de
  otra feature ni de `core:data`.
- `core:data` → `core:domain` (implementa sus interfaces) → `core:model`.
- `:app` une todo: es el único módulo que conoce `core:data` y todas las features.
- `core:model` y `core:domain` son módulos JVM sin Android, así que sus tests son rápidos y no
  necesitan Robolectric.

## Patrones

- **UDF / MVVM**: cada pantalla tiene un `ViewModel` que expone un `StateFlow<UiState>` inmutable
  y recibe acciones (`onAction(Action)`). Los eventos de un solo uso (navegar, mostrar un
  snackbar) se emiten por un `Channel` expuesto como `Flow`.
- **Casos de uso** en `core:domain` (`operator fun invoke`), inyectados con Hilt.
- **Offline-first**: la fuente de verdad es local (Room y DataStore). `SyncRepository` es la
  costura para un backend futuro; hoy `OfflineSyncRepository` devuelve `SyncResult.Disabled`.
- **Navegación type-safe** con rutas `@Serializable` definidas en cada feature
  (`feature/*/navigation`).

## Build

- Catálogo de versiones en `gradle/libs.versions.toml`.
- Convention plugins en `build-logic/convention`:

| Plugin | Qué aplica |
|---|---|
| `hitz.android.application` | AGP app, SDKs, Java 17, Lint |
| `hitz.android.application.compose` | + Compose, Material 3, Roborazzi/Robolectric para tests |
| `hitz.android.library` | AGP library, SDKs, prefijo de recursos por módulo |
| `hitz.android.library.compose` | + Compose y Roborazzi |
| `hitz.android.feature` | librería Compose + Hilt + Navigation + dependencias de core |
| `hitz.android.room` | Room + KSP con esquemas exportados |
| `hitz.hilt` | Hilt + KSP (o solo `hilt-core` en módulos JVM) |
| `hitz.jvm.library` | Kotlin JVM puro |

- AGP 9 con Kotlin integrado: no se aplica `org.jetbrains.kotlin.android`.
- Estilo: **detekt** con `detekt-formatting` (reglas de ktlint) se ejecuta una vez desde el
  proyecto raíz sobre todo el código. Así no depende de la API de variantes que AGP 9 retiró.

## CI/CD (GitHub Actions, capa gratuita)

| Workflow | Cuándo | Qué hace |
|---|---|---|
| `ci.yml` | PR y push a `main` | Temario, detekt, Lint, tests unitarios, build debug y APK como artifact |
| `ui-tests.yml` | Manual y cada noche | Roborazzi (comparar o grabar) y tests instrumentados en emulador |
| `release.yml` | Tag `v*` | AAB y APK firmados y GitHub Release con changelog automático |
| `pr-title.yml` | PR | Título en formato Conventional Commits |

Todos los workflows usan `concurrency` con cancelación y caché de Gradle (solo escritura en
`main`). El repo es público, así que los minutos en runners estándar no tienen límite.
