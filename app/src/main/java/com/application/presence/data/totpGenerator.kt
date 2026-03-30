package com.application.presence.data

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

object TotpGenerator {
    /**
     * Generates a 6-digit TOTP code.
     * @param secret The static secret_key from Supabase (e.g., "J9K8L7M6")
     * @param timeMillis The current system time in milliseconds
     * @param timeStepSeconds The rotation window (10 seconds for our app)
     */
    fun generateCode(secret: String, timeMillis: Long, timeStepSeconds: Long = 10): String {
        // 1. Calculate the current time step
        val timeStep = (timeMillis / 1000) / timeStepSeconds

        // 2. Setup the HMAC-SHA1 cryptographic blender
        val keyBytes = secret.toByteArray(Charsets.UTF_8)
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(keyBytes, "HmacSHA1"))

        // 3. Convert time step to bytes and hash it
        val timeBytes = ByteBuffer.allocate(8).putLong(timeStep).array()
        val hash = mac.doFinal(timeBytes)

        // 4. Dynamic Truncation (Extracting the 6 digits)
        val offset = hash.last().toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % 10.0.pow(6).toInt()

        // Ensure it is always exactly 6 digits (pads with leading zeros if needed)
        return otp.toString().padStart(6, '0')
    }
}