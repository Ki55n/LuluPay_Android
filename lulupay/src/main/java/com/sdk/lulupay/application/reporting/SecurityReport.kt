package com.sdk.lulupay.application.reporting

interface SecurityReport {
    fun onSecurityViolation(message: String)
}
