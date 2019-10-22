package com.tazkrtak.staff.models

import com.tazkrtak.staff.R
import com.tazkrtak.staff.repositories.ClientRepository
import com.tazkrtak.staff.repositories.TransactionRepository
import java.util.concurrent.TimeUnit

data class Conductor(
    override val id: String? = null,
    override val name: String? = null,
    override val password: String? = null,
    val bus: Bus? = null
) : Account {

    override val type: Account.Type = Account.Type.CONDUCTOR

    override suspend fun executeTask(ticket: Ticket, extra: Map<String, Any>): TaskResult {

        val requestedPrice = extra[REQUESTED_PRICE] as Double

        if (ticket.fees!! != requestedPrice) {
            return TaskResult(R.string.fees_matching_error)
        }

        val client = ClientRepository.get(ticket.userNationalId!!)
            ?: return TaskResult(R.string.id_error)

        if (client.totp != ticket.totp) {
            return TaskResult(R.string.expired_ticket)
        }

        if (client.balance!! < ticket.totalFees) {
            return TaskResult(R.string.transaction_balance_error)
        }

        if (client.lastTransactionId != "") {
            val lastTransaction = TransactionRepository.get(client.lastTransactionId!!)
            if (lastTransaction!!.issuer == bus?.id &&
                lastTransaction.timeSinceCreation < THRESHOLD
            ) {
                return TaskResult(
                    R.string.repeated_transaction,
                    "${lastTransaction.timestamp?.toDate()}"
                )
            }
        }

        TransactionRepository.set(client.nationalId!!, bus?.id!!, -ticket.totalFees)
        return TaskResult(R.string.successful_transaction, isSuccess = true)
    }

    companion object Inputs {
        val REQUESTED_PRICE = "requested_price"
        val THRESHOLD = TimeUnit.MINUTES.toSeconds(10)
    }

}