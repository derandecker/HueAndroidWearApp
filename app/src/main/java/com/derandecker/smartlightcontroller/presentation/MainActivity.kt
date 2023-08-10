/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.derandecker.smartlightcontroller.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearAppMain(
                onDismissed = { finishAndRemoveTask() }
            )
        }
    }
}

@Composable
fun WearAppMain(
    navController: NavHostController = rememberSwipeDismissableNavController(),
    onDismissed: () -> Unit
) {
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = AppScreens.LightListMain.name,
    ) {
        composable(route = AppScreens.AuthScreen.name) {
            AuthScreen(
                onNavigateToLightControlScreen = {
                    navController.navigate(AppScreens.LightListMain.name)
                },
                onDismissed = { onDismissed() }
            )
        }
        composable(route = AppScreens.LightListMain.name) {
            LightControl(
                onNavigateToAuthScreen = {
                    navController.navigate(AppScreens.AuthScreen.name)
                }
            )
        }
    }
}
