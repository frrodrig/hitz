package eus.hitz.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import eus.hitz.core.designsystem.component.CastMember
import eus.hitz.core.designsystem.component.HitzScene
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The 24 star challenge scenes with their hosts (as in the course content), at rest. */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w720dp-h560dp-xhdpi")
class SceneScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val hosts = listOf(
        "u01-1" to CastMember.Ane, "u01-2" to CastMember.Unai, "u02-1" to CastMember.Mikel,
        "u02-2" to CastMember.Ane, "u03-1" to CastMember.Maite, "u03-2" to CastMember.Unai,
        "u04-1" to CastMember.Maite, "u04-2" to CastMember.Mikel, "u05-1" to CastMember.Maite,
        "u05-2" to CastMember.Ane, "u06-1" to CastMember.Ane, "u06-2" to CastMember.Unai,
        "u07-1" to CastMember.Mikel, "u07-2" to CastMember.Ane, "u08-1" to CastMember.Unai,
        "u08-2" to CastMember.Maite, "u09-1" to CastMember.Mikel, "u09-2" to CastMember.Ane,
        "u10-1" to CastMember.Mikel, "u10-2" to CastMember.Maite, "u11-1" to CastMember.Mikel,
        "u11-2" to CastMember.Unai, "u12-1" to CastMember.Unai, "u12-2" to CastMember.Maite,
    )

    @Test
    fun scenes() {
        composeRule.setContent {
            HitzTheme {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    hosts.chunked(6).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { (unit, host) ->
                                HitzScene(unit, host, pose = MascotPose.Hola, size = 108.dp, animated = false)
                            }
                        }
                    }
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/scenes.png")
    }
}
