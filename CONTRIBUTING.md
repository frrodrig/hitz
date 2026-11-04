# Cómo contribuir

## Ramas y PR

- `main` está protegida: todo entra por PR y con el CI en verde.
- Nombres de rama: `feat/…`, `fix/…`, `content/…`, `docs/…`, `ci/…`, `build/…`.
- Se fusiona con *squash*, así que **el título del PR es el mensaje del commit** y debe seguir
  [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/). El workflow
  *Título del PR* lo comprueba.

Tipos admitidos: `feat`, `fix`, `docs`, `content` (contenido del curso), `style`, `refactor`,
`perf`, `test`, `build`, `ci`, `chore`, `revert`. El ámbito suele ser el módulo:
`feat(lesson): ejercicio de emparejar parejas`.

## Releases

1. Actualiza `README.md` si cambia el estado de los hitos.
2. Crea un tag `vMAJOR.MINOR.PATCH` en `main` y súbelo: `git tag v0.1.0 && git push origin v0.1.0`.
3. El workflow **Release** compila el AAB y el APK firmados y publica una GitHub Release con
   changelog automático.

### Secretos necesarios (Settings → Secrets and variables → Actions)

| Secreto | Contenido |
|---|---|
| `HITZ_KEYSTORE_BASE64` | El keystore en base64 (`base64 -w0 release.jks`) |
| `HITZ_KEYSTORE_PASSWORD` | Contraseña del keystore |
| `HITZ_KEY_ALIAS` | Alias de la clave |
| `HITZ_KEY_PASSWORD` | Contraseña de la clave |

Los keystores nunca se suben al repo (`*.jks` y `*.keystore` están en `.gitignore`).

## Contenido

Lee [`docs/content-guide.md`](docs/content-guide.md) antes de escribir lecciones. Nunca copies
textos, diálogos, ejercicios ni imágenes del libro de referencia.
