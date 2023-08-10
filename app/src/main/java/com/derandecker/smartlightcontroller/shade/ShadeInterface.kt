package com.derandecker.smartlightcontroller.shade

import android.util.Log
import com.derandecker.smartlightcontroller.LoggerInterface
import com.derandecker.smartlightcontroller.coroutine.CoroutineDispatchProvider
import com.derandecker.smartlightcontroller.database.LightDao
import com.derandecker.smartlightcontroller.database.LightEntity
import com.derandecker.smartlightcontroller.preferences.EncryptedPreferencesInterface
import inkapplications.shade.auth.structures.AppId
import inkapplications.shade.core.Shade
import inkapplications.shade.lights.parameters.LightUpdateParameters
import inkapplications.shade.structures.AuthToken
import inkapplications.shade.structures.ResourceId
import inkapplications.shade.structures.SecurityStrategy
import inkapplications.shade.structures.parameters.PowerParameters
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.ExperimentalTime

interface ShadeInterface {
    suspend fun toggleLight(id: String)

    suspend fun awaitToken(): AuthToken

    suspend fun setAuthToken(hueToken: AuthToken)

    suspend fun setHostnameAndSecurityStrategy(hueHubIp: String, hueHubDeviceId: String?)

    suspend fun addLightsToRoom()

    suspend fun setHub(): Pair<String, String?>
}

class ShadeImpl @Inject constructor(
    private val shade: Shade,
    private val coroutineDispatchProvider: CoroutineDispatchProvider,
    private val lightDao: LightDao,
    private val encryptedSharedPreferences: EncryptedPreferencesInterface,
    private val logger: LoggerInterface,
) : ShadeInterface {
    override suspend fun toggleLight(id: String): Unit =
        withContext(coroutineDispatchProvider.ioDispatcher) {
            val powerStatus = shade.lights.getLight(ResourceId(id)).powerInfo.on
            shade.lights.updateLight(
                ResourceId(id),
                LightUpdateParameters(PowerParameters((!powerStatus)))
            )
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun awaitToken(): AuthToken {
        return shade.auth.awaitToken(
            appId = AppId(
                appName = "decker-hue",
                instanceName = "decker-hue-first"
            ),
        )
    }

    override suspend fun setAuthToken(hueToken: AuthToken) {
        shade.configuration.setAuthToken(hueToken)
    }

    override suspend fun setHostnameAndSecurityStrategy(hueHubIp: String, hueHubDeviceId: String?) {

        hueHubDeviceId?.let {
            SecurityStrategy.HueCa(
                ip = hueHubIp,
                deviceId = it
            )
        }?.let {
            shade.configuration.setSecurityStrategy(
                it
            )
        }

        shade.configuration.setHostname(hostname = hueHubIp)
        shade.configuration.setSecurityStrategy(
            SecurityStrategy.Insecure(
                hostname = hueHubIp,
            ),
        )
        Log.d("ShadeTest", "SecurityStrategy Set")
    }

    override suspend fun addLightsToRoom() {
        val lightList = shade.lights.listLights()

        lightList.forEach {
            lightDao.insertLight(
                LightEntity(
                    it.id.toString(),
                    shade.devices.getDevice(it.owner.id).metadata.name
                )
            )
        }
    }

    override suspend fun setHub(): Pair<String, String?> {

        // discovery.meethue.com gives code 429 if too many requests in a short time
        // recommended approach is mDNS

        var hueHubIp = encryptedSharedPreferences.getString("hueHubIp", "")
        var hueHubDeviceId = encryptedSharedPreferences.getString("hueHubDeviceId", "")

        if (hueHubIp == null || hueHubIp == "") {
            logger.debug("shade", "getting devices")

            val devices = shade.onlineDiscovery.getDevices()

            hueHubIp = devices[0].localIp
            hueHubDeviceId = devices[0].id.toString()

            encryptedSharedPreferences.putString("hueHubIp", hueHubIp)
            encryptedSharedPreferences.putString("hueHubDeviceId", hueHubDeviceId)
        }
        return Pair(hueHubIp, hueHubDeviceId)
    }
}
