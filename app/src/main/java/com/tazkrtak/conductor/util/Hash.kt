package com.tazkrtak.conductor.util

import java.security.MessageDigest

object Hash {

    private const val HEX_CHARS = "0123456789ABCDEF"

    fun sha512(input: String): String {
        return hash("SHA-512", input)
    }

    private fun hash(type: String,input: String): String {

        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())

        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()

    }
}