package eus.hitz.app.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eus.hitz.app.R
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.update.AppRelease

/** Offers a newer version: [onDownload] opens its APK link, [onDismiss] asks again tomorrow. */
@Composable
internal fun UpdateDialog(
    release: AppRelease,
    onDownload: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        icon = { Icon(painterResource(HitzIcons.Star), contentDescription = null) },
        title = { Text(stringResource(R.string.update_title)) },
        text = { Text(stringResource(R.string.update_text, release.version)) },
        confirmButton = { HitzButton(text = stringResource(R.string.update_download), onClick = onDownload) },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.update_later)) } },
    )
}

@HitzPreviews
@Composable
private fun UpdateDialogPreview() {
    HitzTheme {
        UpdateDialog(
            AppRelease("1.2.0", "https://example.org/hitz-1.2.0.apk", "https://example.org/v1.2.0"),
            onDownload = {},
            onDismiss = {},
        )
    }
}
