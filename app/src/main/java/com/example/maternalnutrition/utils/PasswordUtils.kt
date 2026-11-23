package com.example.maternalnutrition.utils


import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    private const val ITERATIONS = 310000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        return salt.joinToString("") {
            "%02x".format(it)
        }
    }

    fun hashPassword(password: String, salt: String): String {
        val saltBytes = hexToBytes(salt)

        val spec = PBEKeySpec(
            password.toCharArray(),
            saltBytes,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            val factory = SecretKeyFactory.getInstance(ALGORITHM)
            val hash = factory.generateSecret(spec).encoded

            hash.joinToString("") {
                "%02x".format(it)
            }
        } finally {
            spec.clearPassword()
        }
    }

    fun verifyPassword(
        enteredPassword: String,
        storedHash: String,
        storedSalt: String
    ): Boolean {
        return try {
            val enteredHash = hashPassword(
                enteredPassword,
                storedSalt
            )

            MessageDigest.isEqual(
                hexToBytes(enteredHash),
                hexToBytes(storedHash)
            )
        } catch (e: Exception) {
            false
        }
    }

    private fun hexToBytes(value: String): ByteArray {
        require(value.length % 2 == 0) {
            "Invalid hexadecimal value"
        }

        return value.chunked(2).map {
            it.toInt(16).toByte()
        }.toByteArray()
    }
}
