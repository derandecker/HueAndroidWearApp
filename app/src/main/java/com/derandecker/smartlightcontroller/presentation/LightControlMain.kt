package com.derandecker.smartlightcontroller.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.derandecker.smartlightcontroller.presentation.theme.SmartLightControllerTheme
import com.derandecker.smartlightcontroller.presentation.viewmodel.LightControlViewModel

@Composable
fun LightControl(
    lightControlViewModel: LightControlViewModel = hiltViewModel(),
    onNavigateToAuthScreen: () -> Unit = {}
) {
    val setupComplete by lightControlViewModel.setupComplete.observeAsState()
    val lightList by lightControlViewModel.lightList.observeAsState()
    when (setupComplete) {
        true -> {}
        false -> {
            LaunchedEffect(setupComplete) {
                onNavigateToAuthScreen()
            }
        }

        else -> {}
    }
    SmartLightControllerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                lightList?.let {
                    items(it) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                            onClick = {
                                lightControlViewModel.toggleLight(it.id)
                            },
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(start = 8.dp, end = 8.dp),
                                textAlign = TextAlign.Center,
                                text = it.name
                            )
                        }
                    }
                }
            }
        }
    }
}
