package eus.hitz.core.model.content

import kotlinx.serialization.json.Json

/** JSON configuration shared by the app and the content tests: strict, so drift fails fast. */
val ContentJson: Json = Json {
    ignoreUnknownKeys = false
    explicitNulls = false
}
