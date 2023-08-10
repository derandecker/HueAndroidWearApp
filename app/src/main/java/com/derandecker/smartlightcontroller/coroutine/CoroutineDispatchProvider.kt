package com.derandecker.smartlightcontroller.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface CoroutineDispatchProvider {
    val ioDispatcher: CoroutineDispatcher
}

class CoroutineDispatchProviderImpl @Inject constructor() : CoroutineDispatchProvider {
    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
}
