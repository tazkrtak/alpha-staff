package com.tazkrtak.staff.models

data class Conductor(
    override val id: String? = null,
    override val name: String? = null,
    override val password: String? = null,
    val bus: Bus? = null
) : Account {
    override val type: Account.Type = Account.Type.CONDUCTOR
}