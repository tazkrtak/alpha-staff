package com.tazkrtak.staff.models

import com.tazkrtak.staff.App
import com.tazkrtak.staff.R
import com.tazkrtak.staff.repositories.BusRepository
import com.tazkrtak.staff.repositories.ClientRepository
import com.tazkrtak.staff.repositories.TransactionRepository
import java.util.concurrent.TimeUnit

data class Collector(
    override val id: String? = null,
    override val name: String? = null,
    override val password: String? = null
) : Account {

    override val type: Account.Type = Account.Type.COLLECTOR

    override suspend fun executeTask(ticket: Ticket, extra: Map<String, Any>): TaskResult {

        val client = ClientRepository.get(ticket.userNationalId!!)
            ?: return TaskResult(R.string.id_error)

        if (client.lastTransactionId != "") {
            val lastTransaction = TransactionRepository.get(client.lastTransactionId!!)

            val bus = BusRepository.get(lastTransaction!!.issuer!!)
            if (bus != null) {
                return TaskResult(
                    MESSAGE_ID,
                    "${getMessage(R.string.issuer)}: ${bus.number}\n" +
                            "${getMessage(R.string.amount)}: ${lastTransaction.amount}\n" +
                            "${getMessage(R.string.since)}: ${TimeUnit.SECONDS.toMinutes(
                                lastTransaction.timeSinceCreation
                            )} " +
                            getMessage(R.string.minute)
                )
            }
        }

        return TaskResult(MESSAGE_ID, getMessage(R.string.no_transaction), isSuccess = false)
    }

    override fun makeTransaction(clientNationalId: String, amount: Double) {
        TODO("not implemented")
    }

    private fun getMessage(id: Int): String {
        return App.appContext!!.getString(id)
    }

    companion object {
        const val MESSAGE_ID = 101
    }

}