package com.sdk.lulupay.storage

import android.content.Context
import android.util.Base64
import com.sdk.lulupay.storage.SecureStorage
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SecureLoginStorage {
    private const val PREFS_NAME = "lulupay_secure_login_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_IV = "password_iv"

    // Save credentials securely
    fun saveLoginDetails(context: Context, username: String, password: String) {
        SecureStorage.generateKey()

        val (encryptedPassword, iv) = SecureStorage.encryptData(password)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, Base64.encodeToString(encryptedPassword, Base64.DEFAULT))
            putString(KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            apply()
        }
    }

    // Retrieve credentials securely
    fun getLoginDetails(context: Context): Pair<String?, String?> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val encryptedPasswordBase64 = sharedPreferences.getString(KEY_PASSWORD, null)
        val ivBase64 = sharedPreferences.getString(KEY_IV, null)

        if (username != null && encryptedPasswordBase64 != null && ivBase64 != null) {
            val encryptedPassword = Base64.decode(encryptedPasswordBase64, Base64.DEFAULT)
            val iv = Base64.decode(ivBase64, Base64.DEFAULT)
            val decryptedPassword = SecureStorage.decryptData(encryptedPassword, iv)
            return Pair(username, decryptedPassword)
        }

        return Pair(null, null)
    }

    // Clear saved credentials
    fun clearLoginDetails(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().clear().apply()
    }
}