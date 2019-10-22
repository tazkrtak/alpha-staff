package com.tazkrtak.staff.models

import android.util.Base64
import com.tazkrtak.staff.BuildConfig
import com.tazkrtak.staff.util.Mode
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Ticket {
    var userNationalId: String? = null
    var totp: String? = null
    var quantity: Int? = null
    var fees: Double? = null
    var isValid: Boolean = false

    val totalFees: Double
        get() = fees!! * quantity!!

    constructor(encryptedData: String) {
        val data = try {
            decrypt(encryptedData)
        } catch (e: Exception) {
            return
        }
        if (validate(data)) {
            isValid = true
            populate(data)
        }
    }

    private fun decrypt(encryptedText: String): String {

        val algorithm = "AES"
        val transformation = "AES/CBC/PKCS5Padding"
        val cipher = Cipher.getInstance(transformation)
        val key = "TAZKRTAKTAZKRTAK"

        val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
        val iv = ByteArray(16)
        val charArray = key.toCharArray()
        (charArray.indices).forEach { iv[it] = charArray[it].toByte() }
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decryptedByteValue = cipher.doFinal(Base64.decode(encryptedText, Base64.NO_PADDING))
        return String(decryptedByteValue)

    }

    private fun populate(qrData: String) {
        val data = qrData.split(";")
        userNationalId = data[0]
        totp = data[1]
        quantity = data[2].toInt()
        fees = data[3].toDouble()
    }

    private fun validate(qrData: String): Boolean {
        val regex = if (BuildConfig.Mode == Mode.TEST) {
            Regex("^\\d{3};\\d{6};([1-9]|[1-4][0-9]|50);([3-9]|10).[0-9]\$")
        } else {
            Regex("^\\d{14};\\d{6};([1-9]|[1-4][0-9]|50);([3-9]|10).[0-9]\$")
        }
        return qrData.matches(regex)
    }
}