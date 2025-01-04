package com.sdk.lulupay.listeners

interface InitializationListener {
    fun onInitializationCompleted()
    fun onInitializationFailed(errorMessage: String)
}
