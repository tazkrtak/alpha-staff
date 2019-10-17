package com.tazkrtak.staff.models

data class Collector(
    override val id: String? = null,
    override val name: String? = null,
    override val password: String? = null
) : Account {
    override val type: Account.Type = Account.Type.COLLECTOR
}