package de.Pruvex.growpal.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import android.os.Build
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object PinStorage {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "GrowPalPinKey"
    private const val PREF_NAME = "growpal_pin_prefs"
    private const val PREF_PIN = "pin_encrypted"
    private const val PREF_IV = "pin_iv"

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(false)
                    .build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            } else {
                throw UnsupportedOperationException("Key generation requires at least API 23 (Marshmallow). Current device API: ${Build.VERSION.SDK_INT}")
            }
        }
    }

    fun savePin(context: Context, pin: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(pin.toByteArray(Charset.forName("UTF-8")))
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(PREF_PIN, Base64.encodeToString(encrypted, Base64.DEFAULT))
            .putString(PREF_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
    }

    fun getPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val encryptedBase64 = prefs.getString(PREF_PIN, null) ?: return null
        val ivBase64 = prefs.getString(PREF_IV, null) ?: return null
        val encrypted = Base64.decode(encryptedBase64, Base64.DEFAULT)
        val iv = Base64.decode(ivBase64, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charset.forName("UTF-8"))
    }

    fun clearPin(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(PREF_PIN).remove(PREF_IV).apply()
    }

    fun hasPin(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.contains(PREF_PIN) && prefs.contains(PREF_IV)
    }
}
