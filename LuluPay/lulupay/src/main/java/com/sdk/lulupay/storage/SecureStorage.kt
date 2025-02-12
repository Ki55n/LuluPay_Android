package com.sdk.lulupay.storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureStorage {
    private const val KEY_ALIAS = "MySecureKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    /**
     * Generates an AES encryption key in the Android Keystore if one doesn't exist
     * Uses AES/GCM/NoPadding transformation for secure encryption
     */
    fun generateKey() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    /**
     * Encrypts string data using the stored AES key
     * @param data String to encrypt
     * @return Pair of encrypted data bytes and initialization vector
     */
    fun encryptData(data: String): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Pair(encryptedData, iv)
    }

    /**
     * Decrypts encrypted data using stored key and initialization vector
     * @param encryptedData Encrypted bytes to decrypt
     * @param iv Initialization vector used during encryption
     * @return Decrypted string
     */
    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return String(cipher.doFinal(encryptedData))
    }

    /**
     * Retrieves the encryption key from Android Keystore
     * @return SecretKey stored with KEY_ALIAS
     */
    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}