package com.sdk.lulupay.reporting

interface SecurityReport {
    fun onSecurityViolation(message: String)
}
