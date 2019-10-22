package com.tazkrtak.staff.models

import com.google.firebase.Timestamp

data class Transaction(
    val id: String? = null,
    val userNationalId: String? = null,
    val issuer: String? = null,
    val amount: Double? = null,
    val timestamp: Timestamp? = null
)