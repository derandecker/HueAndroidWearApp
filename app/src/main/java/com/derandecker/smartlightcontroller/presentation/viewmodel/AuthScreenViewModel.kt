package com.derandecker.smartlightcontroller.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derandecker.smartlightcontroller.LoggerInterface
import com.derandecker.smartlightcontroller.StatusMessage
import com.derandecker.smartlightcontroller.coroutine.CoroutineDispatchProvider
import com.derandecker.smartlightcontroller.preferences.EncryptedPreferencesInterface
import com.derandecker.smartlightcontroller.shade.ShadeInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import inkapplications.shade.structures.AuthToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val logger: LoggerInterface,
    private val shade: ShadeInterface,
    private val encryptedSharedPreferences: EncryptedPreferencesInterface,
    dispatcher: CoroutineDispatchProvider,
) : ViewModel() {

    val statusMessage = mutableStateOf(StatusMessage.SEARCHING_FOR_HUB)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {

//        In future versions, might do most or some of this work in Application.kt
//        so that when the Activity is trashed by the system and reopened by the user,
//        shade won't give the error message "hostname not set". As is, this doesn't
//        seem to be a problem on the AR glasses or Wear OS, because only one app can be open at a time

        viewModelScope.launch(dispatcher.ioDispatcher + coroutineExceptionHandler)
        {
            val (hueHubIp, hueHubDeviceId) = shade.setHub()
            shade.setHostnameAndSecurityStrategy(hueHubIp, hueHubDeviceId)
            setInitialAuthToken()
            if (!encryptedSharedPreferences.getBoolean("setupComplete")) {
                shade.addLightsToRoom()
                encryptedSharedPreferences.putBoolean("setupComplete", true)
                statusMessage.value = StatusMessage.SETUP_COMPLETE
            }
        }
    }

    private suspend fun setInitialAuthToken() {
        var hueToken = encryptedSharedPreferences.getString("hueTokenAppKey", "")?.let {
            AuthToken(
                it,
                encryptedSharedPreferences.getString("hueTokenClientKey", "")
            )
        }
        if (hueToken?.applicationKey == null || hueToken.applicationKey == "") {
            logger.debug("ShadeTest", "Press button on Bridge")
            statusMessage.value = StatusMessage.PRESS_BUTTON

            hueToken = shade.awaitToken()

            encryptedSharedPreferences.putString("hueTokenAppKey", hueToken.applicationKey)
            hueToken.clientKey?.let {
                encryptedSharedPreferences.putString(
                    "hueTokenClientKey",
                    it
                )
            }
            statusMessage.value = StatusMessage.BUTTON_PRESSED
        } else {
            shade.setAuthToken(hueToken = hueToken)
        }
    }
}
