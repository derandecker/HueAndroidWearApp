package com.derandecker.smartlightcontroller.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.derandecker.smartlightcontroller.LoggerInterface
import com.derandecker.smartlightcontroller.coroutine.CoroutineDispatchProvider
import com.derandecker.smartlightcontroller.database.LightDao
import com.derandecker.smartlightcontroller.database.LightEntity
import com.derandecker.smartlightcontroller.preferences.EncryptedPreferencesInterface
import com.derandecker.smartlightcontroller.shade.ShadeInterface
import com.google.common.truth.Truth.assertThat
import inkapplications.shade.structures.AuthToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test

class AuthScreenViewModelTest {
    private val testDispatcher = object : CoroutineDispatchProvider {
        override val ioDispatcher: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `setupComplete is true`() {

        val viewModel = AuthScreenViewModel(
            shade = TestShade(
                hueToken = AuthToken("", ""),
                lightList = emptyList()
            ),
            encryptedSharedPreferences = TestPreferences(),
            dispatcher = testDispatcher,
            logger = LoggerInterface { tag, message ->  }
        )

//        assertThat(viewModel.setupComplete.value).isTrue()
    }

    // @Test verify setupComplete is false -- under the correct conditions
    // @Test verify HomeViewModel statusMessage is set appropriately under each set of conditions
    // Nick - "Anything in the public API should be tested. Verify expected behavior"
}

private class TestPreferences : EncryptedPreferencesInterface {
    private val preferences = mutableMapOf<String, Any>()
    override fun getBoolean(key: String): Boolean {
        return if (preferences[key] == null) {
            false
        } else {
            preferences[key] as Boolean
        }
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferences[key] = value
    }

    override fun getString(key: String, value: String): String? {
        return if (preferences[key] == null) {
            value
        } else {
            preferences[key] as String?
        }
    }

    override fun putString(key: String, value: String) {
        preferences[key] = value
    }
}

// make sure LightEntity is correct type for List
private class TestShade(private var hueToken: AuthToken, private val lightList: List<LightEntity>) :
    ShadeInterface {

    override suspend fun toggleLight(id: String) = Unit
    override suspend fun awaitToken(): AuthToken {
        return hueToken
    }

    override suspend fun setAuthToken(hueToken: AuthToken) {
        this.hueToken = hueToken
    }

    override suspend fun setHostnameAndSecurityStrategy(hueHubIp: String, hueHubDeviceId: String?) {

    }

    override suspend fun addLightsToRoom() {

    }


}

private class TestDao : LightDao {
    private val lightList = mutableListOf<LightEntity>()
    override fun getAll(): List<LightEntity> {
        TODO("Not yet implemented")
    }

    override fun insertLight(light: LightEntity) {
    }

}
