@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

enum class HitzButtonStyle { Primary, Tonal, Outlined, Text }

/** Expressive button sizes. Lesson actions use [Medium]. */
enum class HitzButtonSize(val height: Dp) {
    Small(ButtonDefaults.MinHeight),
    Medium(ButtonDefaults.MediumContainerHeight),
}

/**
 * Material 3 Expressive buttons: they morph their shape when pressed (`ButtonDefaults.shapes()`)
 * and use the expressive size tokens.
 */
@Composable
fun HitzButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: HitzButtonStyle = HitzButtonStyle.Primary,
    size: HitzButtonSize = HitzButtonSize.Medium,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
) {
    val sized = modifier.heightIn(min = size.height)
    val padding = ButtonDefaults.contentPaddingFor(size.height)
    val shapes = ButtonDefaults.shapes()
    val content: @Composable () -> Unit = {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(size.height)),
            )
            Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(size.height)))
        }
        Text(text, style = ButtonDefaults.textStyleFor(size.height))
    }
    when (style) {
        HitzButtonStyle.Primary ->
            Button(onClick = onClick, shapes = shapes, modifier = sized, enabled = enabled, contentPadding = padding) {
                content()
            }
        HitzButtonStyle.Tonal ->
            FilledTonalButton(
                onClick = onClick,
                shapes = shapes,
                modifier = sized,
                enabled = enabled,
                contentPadding = padding,
            ) { content() }
        HitzButtonStyle.Outlined ->
            OutlinedButton(
                onClick = onClick,
                shapes = shapes,
                modifier = sized,
                enabled = enabled,
                contentPadding = padding,
            ) {
                content()
            }
        HitzButtonStyle.Text ->
            TextButton(
                onClick = onClick,
                shapes = shapes,
                modifier = sized,
                enabled = enabled,
                contentPadding = padding,
            ) {
                content()
            }
    }
}

@HitzPreviews
@Composable
private fun HitzButtonPreview() {
    HitzTheme {
        Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            HitzButton("Egiaztatu", {}, Modifier.fillMaxWidth())
            HitzButton("Saltatu", {}, Modifier.fillMaxWidth(), style = HitzButtonStyle.Tonal)
            HitzButton("Entzun", {}, Modifier.fillMaxWidth(), HitzButtonStyle.Outlined, icon = HitzIcons.Speaker)
            HitzButton("Gero", {}, style = HitzButtonStyle.Text, size = HitzButtonSize.Small)
            HitzButton("Egiaztatu", {}, Modifier.fillMaxWidth(), enabled = false)
        }
    }
}
