package com.tazkrtak.staff.models

data class Collector(
    override val id: String? = null,
    override val name: String? = null,
    override val password: String? = null
) : Account {

    override val type: Account.Type = Account.Type.COLLECTOR

    override suspend fun executeTask(ticket: Ticket, extra: Map<String, Any>): TaskResult {
        TODO("not implemented")
    }

}