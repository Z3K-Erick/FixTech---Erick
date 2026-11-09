package com.fixtech.integration

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricAuthHelper {

    private const val PREFS_NAME = "fixtech_biometric_prefs"
    private const val KEY_USER_BIOMETRIC = "user_biometric_enabled"
    private const val KEY_TECH_BIOMETRIC = "tech_biometric_enabled"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Consulta el estado de activación local de la huella digital.
     * Por defecto retorna false.
     */
    fun isBiometricEnabled(context: Context, isTech: Boolean): Boolean {
        val key = if (isTech) KEY_TECH_BIOMETRIC else KEY_USER_BIOMETRIC
        return getPrefs(context).getBoolean(key, false)
    }

    /**
     * Guarda el estado de activación local de la huella digital.
     */
    fun setBiometricEnabled(context: Context, isTech: Boolean, enabled: Boolean) {
        val key = if (isTech) KEY_TECH_BIOMETRIC else KEY_USER_BIOMETRIC
        getPrefs(context).edit().putBoolean(key, enabled).apply()
    }

    /**
     * Verifica si el hardware soporta y tiene configurada biometría fuerte o débil.
     */
    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Devuelve una explicación amigable cuando canAuthenticate retorna false.
     */
    fun getHardwareErrorMessage(context: Context): String {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "Este dispositivo no cuenta con sensor biométrico."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "El sensor biométrico no está disponible actualmente."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No tienes ninguna huella registrada en el dispositivo. Configúrala en Ajustes."
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> "Se requiere una actualización de seguridad para utilizar la biometría."
            else -> "Autenticación biométrica no compatible o no disponible."
        }
    }

    /**
     * Lanza el diálogo nativo de BiometricPrompt para captura de huella digital.
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val executor = ContextCompat.getMainExecutor(activity)

            val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setNegativeButtonText("Cancelar")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)

            if (subtitle.isNotBlank()) {
                promptInfoBuilder.setSubtitle(subtitle)
            }

            val promptInfo = promptInfoBuilder.build()

            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        // Manejo de callbacks seguro sin cierres inesperados
                        onError(errString.toString())
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // BiometricPrompt maneja visualmente el intento fallido ("No reconocido, reintente")
                    }
                }
            )

            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Error al iniciar el sensor biométrico.")
        }
    }

    /**
     * Utilidad para obtener FragmentActivity de forma segura a partir de cualquier Context.
     */
    fun findFragmentActivity(context: Context): FragmentActivity? {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is FragmentActivity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }
}
