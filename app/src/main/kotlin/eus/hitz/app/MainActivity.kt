package eus.hitz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eus.hitz.app.ui.HitzApp
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
            HitzTheme {
                if (start != null) HitzApp(startWithOnboarding = start)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onForeground()
    }
}
