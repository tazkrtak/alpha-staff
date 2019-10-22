package com.tazkrtak.staff.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.models.Client
import kotlinx.coroutines.tasks.await

object ClientRepository {

    val COLLECTION = "users"

    suspend fun get(nationalId: String): Client? {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection(COLLECTION).document(nationalId).get().await()
        return doc.toObject(Client::class.java)
    }

}