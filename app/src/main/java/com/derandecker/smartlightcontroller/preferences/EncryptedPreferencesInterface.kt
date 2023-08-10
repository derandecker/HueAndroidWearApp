package com.derandecker.smartlightcontroller.preferences

import android.content.SharedPreferences
import javax.inject.Inject

interface EncryptedPreferencesInterface {

    fun getBoolean(key: String): Boolean

    fun putBoolean(key: String, value: Boolean)

    fun getString(key: String, value: String): String?

    fun putString(key: String, value: String)
}

class EncryptedPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    EncryptedPreferencesInterface {

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun putBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun getString(key: String, value: String): String? {
        return sharedPreferences.getString(key, value)
    }

    override fun putString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }
}
