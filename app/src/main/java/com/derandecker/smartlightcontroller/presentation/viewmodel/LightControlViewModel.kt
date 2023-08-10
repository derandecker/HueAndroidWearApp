package com.derandecker.smartlightcontroller.presentation.viewmodel;

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derandecker.smartlightcontroller.database.LightDao
import com.derandecker.smartlightcontroller.database.LightEntity
import com.derandecker.smartlightcontroller.preferences.EncryptedPreferencesInterface
import com.derandecker.smartlightcontroller.shade.ShadeInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import inkapplications.shade.structures.AuthToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LightControlViewModel @Inject constructor(
    private val lightDao: LightDao,
    private val shade: ShadeInterface,
    private val encryptedSharedPreferences: EncryptedPreferencesInterface,
) : ViewModel() {

    private val _setupComplete: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val setupComplete: LiveData<Boolean>
        get() = _setupComplete

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val _lightList = MutableLiveData<List<LightEntity>>()
    val lightList: LiveData<List<LightEntity>>
        get() = _lightList

    init {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (encryptedSharedPreferences.getBoolean("setupComplete")) {
                Log.d("LightControl viewModelScope", "setupComplete posted true")
                val (hueHubIp, hueHubDeviceId) = shade.setHub()
                shade.setHostnameAndSecurityStrategy(hueHubIp, hueHubDeviceId)
                val hueToken = encryptedSharedPreferences.getString("hueTokenAppKey", "")?.let {
                    AuthToken(
                        it,
                        encryptedSharedPreferences.getString("hueTokenClientKey", "")
                    )
                }
                if (hueToken != null) {
                    shade.setAuthToken(hueToken)
                }
                _setupComplete.postValue(true)
                _lightList.postValue(lightDao.getAll())
            } else {
                Log.d("LightControl viewModelScope", "setupComplete posted false")
                _setupComplete.postValue(false)
            }
        }
    }

    fun toggleLight(id: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            shade.toggleLight(id)
        }
    }
}
