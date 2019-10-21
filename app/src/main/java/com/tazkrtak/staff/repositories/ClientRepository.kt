package com.tazkrtak.staff.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.models.Client
import kotlinx.coroutines.tasks.await

object ClientRepository {

    suspend fun get(nationalId: String): Client? {

        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("users").document(nationalId).get().await()

        if (!doc.exists()) return null

        return doc.toObject(Client::class.java)!!
    }
}