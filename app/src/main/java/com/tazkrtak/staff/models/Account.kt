package com.tazkrtak.staff.models

import androidx.core.text.isDigitsOnly

interface Account {

    val id: String?
    val name: String?
    val password: String?
    val type: Type

    suspend fun executeTask(ticket: Ticket, extra: Map<String, Any>): TaskResult

    enum class Type { CONDUCTOR, COLLECTOR }

    companion object {
        fun typeOf(id: String): Type {
            return if (!id.isDigitsOnly()) Type.CONDUCTOR else Type.COLLECTOR
        }
    }

}