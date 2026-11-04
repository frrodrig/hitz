package eus.hitz.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

enum class HitzButtonStyle { Primary, Tonal, Outlined, Text }

private val ButtonMinHeight = 52.dp
private val ButtonPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)

/** Standard Material 3 buttons with the Hitz size and an optional leading icon. */
@Composable
fun HitzButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: HitzButtonStyle = HitzButtonStyle.Primary,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
) {
    val sized = modifier.heightIn(min = ButtonMinHeight)
    val content: @Composable () -> Unit = {
        if (icon != null) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(HitzSpacing.sm))
        }
        Text(text)
    }
    when (style) {
        HitzButtonStyle.Primary ->
            Button(onClick, sized, enabled, contentPadding = ButtonPadding) { content() }
        HitzButtonStyle.Tonal ->
            FilledTonalButton(onClick, sized, enabled, contentPadding = ButtonPadding) { content() }
        HitzButtonStyle.Outlined ->
            OutlinedButton(onClick, sized, enabled, contentPadding = ButtonPadding) { content() }
        HitzButtonStyle.Text ->
            TextButton(onClick, sized, enabled, contentPadding = ButtonPadding) { content() }
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
            HitzButton("Gero", {}, style = HitzButtonStyle.Text)
            HitzButton("Egiaztatu", {}, Modifier.fillMaxWidth(), enabled = false)
        }
    }
}
