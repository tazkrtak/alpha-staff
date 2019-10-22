package com.tazkrtak.staff.models

import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator

data class Client(
    var name: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var nationalId: String? = null,
    var balance: Double? = null,
    var secret: String? = null,
    var lastTransactionId: String? = null
) {
    val totp: String
        get() = GoogleAuthenticator(secret!!).generate()
}