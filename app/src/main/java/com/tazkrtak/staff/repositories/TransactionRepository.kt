package com.tazkrtak.staff.repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.models.Transaction
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

object TransactionRepository {

    // Range of threshold
    val THRESHOLD = TimeUnit.MINUTES.toSeconds(10)

    suspend fun get(transactionId: String): Transaction? {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("transactions").document(transactionId).get().await()

        if (!doc.exists()) return null

        return doc.toObject(Transaction::class.java)!!
    }

    fun set(transaction: Transaction) {
        val db = FirebaseFirestore.getInstance()
        db.runBatch { batch ->

            // Update user balance
            batch.update(
                db.collection("users")
                    .document(transaction.userNationalId!!),
                "balance", FieldValue.increment(transaction.amount!!)
            )

            // Update user's last withdrawal
            batch.update(
                db.collection("users")
                    .document(transaction.userNationalId!!),
                "lastTransactionId", transaction.id
            )

            // Push transaction Document
            batch.set(
                db.collection("transactions")
                    .document(transaction.id!!),
                transaction
            )
        }

    }

}