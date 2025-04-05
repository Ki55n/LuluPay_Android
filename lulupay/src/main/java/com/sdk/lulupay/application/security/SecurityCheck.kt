package com.sdk.lulupay.application.security

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.GET_SIGNATURES
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sdk.lulupay.application.reporting.SecurityReport
import java.io.File

class SecurityCheck(private val sdkContext: Context, private val securityReport: SecurityReport) {

    private val handler = Handler(Looper.getMainLooper())
    private val sdkPackageName = "com.sdk.lulupay" // SDK's package name
    private var securityCheckInterval: Long = 5000 // Security check interval in milliseconds

    init {
        startSecurityLoop()
    }

    private fun startSecurityLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                performSecurityCheck()
                handler.postDelayed(this, securityCheckInterval)
            }
        }, securityCheckInterval)
    }

    // Perform the security check and return whether it's successful
    fun performSecurityCheck(): Boolean {
        val failedChecks = mutableListOf<String>()

        if (isDeviceRooted()) {
            failedChecks.add("Device is rooted")
        }
        if (isMemoryTampered()) {
            failedChecks.add("Memory is tampered (e.g., with Frida, Xposed, or Magisk)")
        }
        if (isSignatureInvalid()) {
            failedChecks.add("App signature is invalid")
        }
        if (isPackageNameModified()) {
            failedChecks.add("Package name has been modified")
        }

        return if (failedChecks.isNotEmpty()) {
            // Join all failed checks into a single message
            val failureMessage = "Security check failed! Issues: ${failedChecks.joinToString(", ")}"
            securityReport.onSecurityViolation(failureMessage)
            Log.e("Security Alert", failureMessage) // Log detailed failure info
            false
        } else {
            true
        }
    }

    private fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su"
        )
        return paths.any { path -> File(path).exists() }
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
            val packageName = sdkContext.packageName // Using SDK context here
            val packageManager = sdkContext.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, GET_SIGNATURES)
            val signatures = packageInfo.signatures
            if (signatures?.isNotEmpty() == true) {
                Log.d("SDK signature $packageName", signatures[0].toCharsString())
                return signatures[0].toCharsString() // Get the first signature (in case there are multiple)
            }
        } catch (e: Exception) {
            Log.e("SecurityCheck", "Error getting app signature: ${e.message}")
        }
        return "DUMMY_SIGNATURE" // Return a dummy signature in case of error
    }

    private fun isPackageNameModified(): Boolean {
        // Check if the current package name is not the SDK package name
        return sdkContext.packageName != sdkPackageName
    }
}
