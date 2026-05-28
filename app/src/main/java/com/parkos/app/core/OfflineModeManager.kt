package com.parkos.app.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineModeManager @Inject constructor() {

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode

    fun setOfflineMode(value: Boolean) {
        _isOfflineMode.value = value
    }
}