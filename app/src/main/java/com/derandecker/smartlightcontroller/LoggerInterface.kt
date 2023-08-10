package com.derandecker.smartlightcontroller

import android.util.Log
import javax.inject.Inject

fun interface LoggerInterface {
    fun debug(tag: String, message: String)
}

class LoggerInterfaceImpl @Inject constructor() : LoggerInterface {
    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

}
