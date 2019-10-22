package com.tazkrtak.staff.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Transaction(
    val id: String? = null,
    val userNationalId: String? = null,
    val issuer: String? = null,
    val amount: Double? = null,
    val timestamp: Timestamp? = null
) {
    @get:Exclude
    val timeSinceCreation
        get() = Timestamp.now().seconds - timestamp!!.seconds
}