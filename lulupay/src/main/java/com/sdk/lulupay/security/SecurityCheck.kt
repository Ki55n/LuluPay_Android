package com.sdk.lulupay.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.sdk.lulupay.listeners.SecurityReportListener
import com.sdk.lulupay.report.SecurityReport
import java.io.File
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.system.exitProcess

class SecurityCheck(private val sdkContext: Context) {
    private var context: Context = sdkContext

    private val handler = Handler(Looper.getMainLooper())
    private val sdkPackageName = "com.sdk.lulupay" // SDK's package name
    private var securityCheckInterval: Long = 5000 // Security check interval in milliseconds
    private var reportMsg: String = ""

    init {
        startSecurityLoop()
    }

    companion object {
        lateinit var reportListeners: SecurityReportListener
        fun setSecurityReportListener(reportListener: SecurityReportListener) {
            reportListeners = reportListener
        }
    }

    fun startSecurityLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isSecurityCheckSuccessful()) {
                    report()
                    exitProcess(0)
                }
                handler.postDelayed(this, securityCheckInterval)
            }
        }, securityCheckInterval)
    }

    // Perform the security check and return whether it's successful
    private fun isSecurityCheckSuccessful(): Boolean {

        if (isDeviceRooted()) {
            return false
        }
        if (isMemoryTampered()) {
            return false
        }
       if (isSignatureInvalid()) {
            return false
        }
        if (isPackageNameModified()) {
            return false
        }

        return true
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
        val sha256Signature = "3E:6B:2B:C2:64:B5:91:54:3C:B5:B7:14:4A:6E:A9:18:2D:74:2A:69:FB:38:90:F7:B2:9D:EE:6B:60:67:53:CF"
        val sha1Signature = "22:84:E6:82:45:D7:17:61:EF:84:E8:72:D4:42:51:19:9A:59:A1:2D"
        return getSHA256Signature(context) != sha256Signature || getSHA1Signature(context) != sha1Signature
    }

    private fun getSHA1Signature(context: Context): String? {
        return getSignatureDigest(context, "SHA1")
    }

    private fun getSHA256Signature(context: Context): String? {
        return getSignatureDigest(context, "SHA-256")
    }

    private fun getSignatureDigest(context: Context, algorithm: String): String? {
        return try {
            val packageName = context.packageName
            val packageManager = context.packageManager

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // API 28+
                val packageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                // API 24–27
                val packageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
                packageInfo.signatures
            }

            if (signatures.isNullOrEmpty()) return null

            val certFactory = CertificateFactory.getInstance("X.509")
            val cert = certFactory.generateCertificate(
                signatures[0]?.toByteArray()?.inputStream()
            ) as X509Certificate
            val md = MessageDigest.getInstance(algorithm)
            val publicKey = md.digest(cert.encoded)

            // Return colon-separated hex format: AA:BB:CC...
            publicKey.joinToString(":") { byte -> "%02X".format(byte) }
        } catch (e: Exception) {
            Log.e("AppSignature", "$algorithm error: ${e.message}")
            null
        }
    }


    private fun isPackageNameModified(): Boolean {
        val expectedPackageName = SecurityCheck::class.java.`package`?.name ?: return true
        val isModified = !expectedPackageName.contains( sdkPackageName)
        Log.d("SecurityCheck", "Expected: $expectedPackageName, Configured: $sdkPackageName")
        return isModified
    }

    private fun report() {
        reportListeners.onReportMsg(reportMsg)
    }
}
