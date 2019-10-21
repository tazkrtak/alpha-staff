package com.tazkrtak.staff.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.App
import com.tazkrtak.staff.R
import com.tazkrtak.staff.models.Conductor
import com.tazkrtak.staff.models.ScanResult
import com.tazkrtak.staff.models.Ticket
import com.tazkrtak.staff.models.Transaction
import com.tazkrtak.staff.util.Auth

object ConductorRepository {

    suspend fun makeScan(lastText: String, selectedTicketPrice: Double): ScanResult {
        val scanResult = ScanResult()

        val ticket = Ticket(lastText)
        if (!ticket.isValid!!) {
            scanResult.message = getMessage(R.string.ticket_validation_error)
            return scanResult
        }

        val client = ClientRepository.get(ticket.userNationalId!!)
        if (client == null) {
            scanResult.message = getMessage(R.string.id_error)

            return scanResult
        }

        if (client.totp != ticket.totp) {
            scanResult.message = getMessage(R.string.ticket_validation_error)
            return scanResult
        }


        if (ticket.fees!! != selectedTicketPrice) {
            scanResult.message = getMessage(R.string.fees_matching_error)
            return scanResult
        }


        if (client.balance!! < ticket.totalFees) {
            scanResult.message = getMessage(R.string.transaction_balance_error)
            return scanResult
        }
        if (client.lastTransactionId != "") {
            val lastTransaction =
                TransactionRepository.get(client.lastTransactionId!!)

            if (lastTransaction!!.issuer == ((Auth.currentUser) as Conductor).bus!!.id &&
                (Timestamp.now().seconds - lastTransaction.timestamp!!.seconds) < TransactionRepository.THRESHOLD
            ) {

                scanResult.message =
                    "${getMessage(R.string.repeated_transaction)}\n${lastTransaction.timestamp.toDate()}"
                return scanResult
            }
        }

        val db = FirebaseFirestore.getInstance()
        val transactionDoc = db.collection("transactions").document()
        val transaction = Transaction(
            transactionDoc.id,
            ticket.userNationalId,
            ((Auth.currentUser) as Conductor).bus!!.id,
            (-1 * ticket.totalFees),
            Timestamp.now()
        )

        TransactionRepository.set(transaction)
        scanResult.message = getMessage(R.string.successful_transaction)
        scanResult.isSuccess = true

        return scanResult
    }


    private fun getMessage(id: Int): String {
        return App.appContext!!.getString(id)
    }

}