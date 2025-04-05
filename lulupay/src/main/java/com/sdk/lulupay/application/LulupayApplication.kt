package com.sdk.lulupay.application

import android.app.Activity
import android.app.Application
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sdk.lulupay.application.security.SecurityCheck
import com.sdk.lulupay.application.reporting.SecurityReport

class LulupayApplication : Application(), SecurityReport {

    private lateinit var securityCheck: SecurityCheck

    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()

        // Track activity lifecycle to get the current activity context
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActivity = activity
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (currentActivity == activity) {
                    currentActivity = null
                }
            }

            // Other lifecycle methods can be left empty
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })

        // Initialize SecurityCheck with the Application context
        securityCheck = SecurityCheck(this, this)

        // Perform the security check
        if (!securityCheck.performSecurityCheck()) {
            // If the check fails, post a runnable to show the dialog in the main thread
            Handler(Looper.getMainLooper()).post {
                showSecurityViolationDialog()
            }
        }

        Log.d("LulupayApplication", "SecurityCheck initialized at app start")
    }

    private fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    // Show alert dialog for security violation and exit the app
    private fun showSecurityViolationDialog() {
        val activity = getCurrentActivity()
        if (activity != null) {
            // Use the activity context to show the dialog
            val builder = AlertDialog.Builder(activity)
                .setTitle("Security Violation")
                .setMessage("Security check failed. The app will now exit.")
                .setCancelable(false) // Make it non-cancellable (requires user to acknowledge)
                .setPositiveButton("Exit") { dialog, _ ->
                    // Log the security violation
                    Log.e("Security Alert", "Security violation detected! Exiting the app.")

                    // Close the app after the user presses exit
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(1) // Exit the app
                }

            // Show the dialog
            builder.show()
        } else {
            // If no activity context is available, show a toast as fallback
            Log.e("Security Alert", "No valid activity context available. Exiting the app.")
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }

    override fun onSecurityViolation(message: String) {
        // Handle security violations (log message)
        Log.e("Security Alert", message)
    }
}
