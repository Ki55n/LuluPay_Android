package com.sdk.lulupay.singleton

import com.sdk.lulupay.listeners.FinishActivityListener

object ActivityCloseManager {
    private val listeners = mutableListOf<FinishActivityListener>()

    fun registerListener(listener: FinishActivityListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: FinishActivityListener) {
        listeners.remove(listener)
    }

    fun closeAll() {
        listeners.forEach { it.onFinishActivity() }
        listeners.clear() // Clear the list after closing activities
    }
}