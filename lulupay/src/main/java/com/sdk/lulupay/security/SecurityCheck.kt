package com.sdk.lulupay.security

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_SIGNATURES
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sdk.lulupay.reporting.SecurityReport

class SecurityCheck(private val context: Context, private val securityReport: SecurityReport) {

    private val handler = Handler(Looper.getMainLooper())

    init {
        // Delay the check for 1 second
        handler.postDelayed({ performSecurityCheck() }, 1000)
    }

    private fun performSecurityCheck() {
        if (isDeviceRooted() || isMemoryTampered() || isSignatureInvalid() || isPackageNameModified()) {
            securityReport.onSecurityViolation("Security check failed!")
        }
    }

    private fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su"
        )
        return paths.any { path -> java.io.File(path).exists() }
    }

    private fun isMemoryTampered(): Boolean {
        val suspiciousProcesses = listOf("frida", "xposed", "magisk")
        val processList = Runtime.getRuntime().exec("ps").inputStream.bufferedReader().readText()
        return suspiciousProcesses.any { processList.contains(it, ignoreCase = true) }
    }

    private fun isSignatureInvalid(): Boolean {
        // Replace with the actual app signature hash
        val validSignature = "+G02M1F5nNd+VrcApo/1pgmjG8xKx6eQf6Owr79KIYc="
        val currentSignature = getAppSignature()
        return currentSignature != validSignature
    }

    private fun getAppSignature(): String {
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, GET_SIGNATURES)
            val signatures = packageInfo.signatures
            if (signatures?.isNotEmpty() == true) {
                return signatures[0].toCharsString() // Get the first signature (in case there are multiple)
            }
        } catch (e: Exception) {
            Log.e("SecurityCheck", "Error getting app signature: ${e.message}")
        }
        return "DUMMY_SIGNATURE" // Return a dummy signature in case of error
    }

    private fun isPackageNameModified(): Boolean {
        return context.packageName == "com.sdk.lulupay"
    }
}
