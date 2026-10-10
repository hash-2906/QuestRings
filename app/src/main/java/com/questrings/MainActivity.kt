package com.questrings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.domain.model.xp.AppEvent
import com.questrings.domain.model.xp.MedalUnlockEvent
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.navigation.QuestNavHost
import com.questrings.presentation.medal.MedalUnlockDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var observeCurrentUserUseCase: ObserveCurrentUserUseCase

    @Inject
    lateinit var appEventBus: MutableSharedFlow<AppEvent>

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Notification permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()

        setContent {
            QuestRingsTheme {
                val currentUser by observeCurrentUserUseCase().collectAsState(initial = null)
                var activeMedalUnlock by remember { mutableStateOf<MedalUnlockEvent?>(null) }

                LaunchedEffect(Unit) {
                    appEventBus.collect { event ->
                        when (event) {
                            is AppEvent.MedalUnlocked -> {
                                activeMedalUnlock = event.event
                            }
                            is AppEvent.LevelUp -> {
                                // Level up event received
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundPrimary
                ) {
                    QuestNavHost(
                        observeCurrentUserUseCase = observeCurrentUserUseCase,
                        onGoogleSignInTrigger = {
                            // Trigger Google Sign-In flow via Credential Manager
                        }
                    )

                    activeMedalUnlock?.let { unlockEvent ->
                        MedalUnlockDialog(
                            event = unlockEvent,
                            userName = currentUser?.displayName ?: "Adventurer",
                            avatarColorHex = currentUser?.avatarColorHex ?: "#7E5DFF",
                            onDismiss = { activeMedalUnlock = null }
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(permission)
            }
        }
    }
}
