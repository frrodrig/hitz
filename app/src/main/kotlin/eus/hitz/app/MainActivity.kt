package eus.hitz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eus.hitz.app.ui.HitzApp
import eus.hitz.app.ui.UpdateDialog
import eus.hitz.core.designsystem.theme.HitzTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // The splash screen stays until we know whether this is the first run.
        installSplashScreen().setKeepOnScreenCondition { viewModel.startWithOnboarding.value == null }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Read once: finishing the introduction must not rebuild the navigation graph.
            val startWithOnboarding by viewModel.startWithOnboarding.collectAsStateWithLifecycle()
            val start = remember(startWithOnboarding == null) { startWithOnboarding }
            val update by viewModel.update.collectAsStateWithLifecycle()
            val uriHandler = LocalUriHandler.current
            HitzTheme {
                if (start != null) HitzApp(startWithOnboarding = start)
                update?.let { release ->
                    UpdateDialog(
                        release = release,
                        onDownload = {
                            // The browser downloads the APK; opening it installs the update.
                            runCatching { uriHandler.openUri(release.apkUrl) }
                                .onFailure { runCatching { uriHandler.openUri(release.pageUrl) } }
                            viewModel.dismissUpdate()
                        },
                        onDismiss = viewModel::dismissUpdate,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onForeground()
    }
}
