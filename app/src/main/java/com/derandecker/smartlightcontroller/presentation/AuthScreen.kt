package com.derandecker.smartlightcontroller.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import com.derandecker.smartlightcontroller.R
import com.derandecker.smartlightcontroller.StatusMessage
import com.derandecker.smartlightcontroller.presentation.theme.SmartLightControllerTheme
import com.derandecker.smartlightcontroller.presentation.viewmodel.AuthScreenViewModel

@Composable
fun AuthScreen(
    onNavigateToLightControlScreen: () -> Unit = {},
    onDismissed: () -> Unit
) {
    val authScreenViewModel: AuthScreenViewModel = hiltViewModel()
    val statusMessage = authScreenViewModel.statusMessage.value

    when (statusMessage) {
        StatusMessage.SETUP_COMPLETE -> {
            LaunchedEffect(statusMessage) {
                onNavigateToLightControlScreen()
            }
        }

        else -> {}
    }

    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(),
        onDismissed = { onDismissed() }
    ) {
        SmartLightControllerTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    text = when (statusMessage) {
                        StatusMessage.SEARCHING_FOR_HUB -> stringResource(id = R.string.searching_for_hue_hub)
                        StatusMessage.PRESS_BUTTON -> stringResource(id = R.string.press_button_on_bridge)
                        StatusMessage.BUTTON_PRESSED -> stringResource(id = R.string.button_pressed_communicating_with_hub)
                        StatusMessage.SETUP_COMPLETE -> stringResource(id = R.string.setup_complete)
                    }
                )
            }
        }
    }
}

enum class AppScreens(@StringRes val title: Int) {
    AuthScreen(title = R.string.main),
    LightListMain(title = R.string.light_list_screen),
}
