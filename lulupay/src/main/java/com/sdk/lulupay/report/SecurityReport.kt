package com.sdk.lulupay.report

import com.sdk.lulupay.listeners.SecurityReportListener
import com.sdk.lulupay.security.SecurityCheck

class SecurityReport : SecurityReportListener {
    companion object {
        fun setSecurityReportListener() {
            // Set the listener for security report events
            SecurityCheck.setSecurityReportListener(SecurityReport())
        }
    }

    override fun onReportMsg(message: String) {
        TODO("Not yet implemented")
    }
}
