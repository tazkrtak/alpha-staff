package com.tazkrtak.staff.models

import com.google.firebase.firestore.Exclude
import com.tazkrtak.staff.R
import com.tazkrtak.staff.activities.MainActivity
import com.tazkrtak.staff.repositories.ClientRepository
import com.tazkrtak.staff.repositories.TransactionRepository
import java.util.concurrent.TimeUnit

data class Conductor(
    override val id: String? = null,
    override val name: String? = null,
    override val password: String? = null,
    @get:Exclude
    var bus: Bus? = null
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
                    "${lastTransaction.timestamp?.toDate()}",
                    extra = mapOf(
                        CLIENT_ID to client.nationalId!!,
                        FEES to ticket.totalFees
                    )
                )
            }
        }

        makeTransaction(client.nationalId!!, ticket.totalFees)
        return TaskResult(R.string.successful_transaction, isSuccess = true)
    }

    override fun makeTransaction(clientNationalId: String, amount: Double) {
        MainActivity.ticketsCount++
        TransactionRepository.set(clientNationalId, bus?.id!!, -amount)
    }

    companion object Inputs {
        const val REQUESTED_PRICE = "requested_price"
        const val CLIENT_ID = "clientNationalId"
        const val FEES = "totalFees"
        val THRESHOLD = TimeUnit.MINUTES.toSeconds(10)
    }

}