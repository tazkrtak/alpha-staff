package com.tazkrtak.staff.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.models.Bus
import kotlinx.coroutines.tasks.await

object BusRepository {
    private const val COLLECTION = "buses"

    suspend fun get(id: String): Bus? {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection(COLLECTION).document(id).get().await()
        return doc.toObject(Bus::class.java)
    }
}