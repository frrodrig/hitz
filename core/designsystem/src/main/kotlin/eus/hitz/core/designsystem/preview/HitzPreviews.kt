package eus.hitz.core.designsystem.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/** Light, dark, 200 % font scale and Basque locale previews. */
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Font 200 %", showBackground = true, fontScale = 2f)
@Preview(name = "Basque", showBackground = true, locale = "eu")
annotation class HitzPreviews
