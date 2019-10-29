package com.tazkrtak.staff.util

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.R
import com.tazkrtak.staff.models.Account
import com.tazkrtak.staff.models.AuthResult
import com.tazkrtak.staff.models.Collector
import com.tazkrtak.staff.models.Conductor
import com.tazkrtak.staff.repositories.BusRepository
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

object Auth {

    private const val ID = "id"
    private const val PASSWORD = "password"

    var currentUser: Account? = null

    suspend fun isSignedIn(): Boolean {
        if (currentUser != null) return true
        val data = getPrefs()
        if (!data[ID].isNullOrEmpty() && !data[PASSWORD].isNullOrEmpty()) {
            val result = fetchData(data[ID].toString(), data[PASSWORD].toString())
            return result.isSuccess
        }
        return false
    }

    suspend fun signIn(id: String, password: String): AuthResult {

        if (id.isEmpty()) {
            return AuthResult(R.string.id_required, target = AuthResult.Target.ID)
        }

        if (password.isEmpty()) {
            return AuthResult(R.string.password_required, target = AuthResult.Target.PASSWORD)
        }

        val hashedPassword = hash(password)
        val result = fetchData(id, hashedPassword)
        if (result.isSuccess) {
            setPrefs(id, hashedPassword)
        }
        return result
    }

    fun signOut() {
        setPrefs(null, null)
        currentUser = null
    }

    private suspend fun getAccountFromDocument(id: String, doc: DocumentSnapshot): Account? {
        return if (Account.typeOf(id) == Account.Type.CONDUCTOR) {
            doc.toObject(Conductor::class.java).also {
                it!!.bus = BusRepository.get(doc["busId"].toString())
            }
        } else {
            doc.toObject(Collector::class.java)
        }
    }

    private suspend fun fetchData(id: String, hashedPassword: String): AuthResult {

        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("staff").document(id).get().await()

        if (!doc.exists()) {
            return AuthResult(R.string.id_error, target = AuthResult.Target.ID)
        }

        val docPassword = doc.data?.get(PASSWORD)?.toString()
        if (docPassword != hashedPassword) {
            return AuthResult(R.string.password_error, target = AuthResult.Target.PASSWORD)
        }

        currentUser = getAccountFromDocument(id, doc)
        return AuthResult(isSuccess = true)
    }

    private fun getPrefs(): Map<String, String> {
        return mapOf(
            ID to SharedPref.getString(ID),
            PASSWORD to SharedPref.getString(PASSWORD)
        )
    }

    private fun setPrefs(id: String?, hashedPassword: String?) {
        SharedPref.addString(ID, id)
        SharedPref.addString(PASSWORD, hashedPassword)
    }

    private fun hash(password: String): String {

        val hexChars = "0123456789abcdef"

        val bytes = MessageDigest
            .getInstance("SHA-512")
            .digest(password.toByteArray())

        val hash = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            hash.append(hexChars[i shr 4 and 0x0f])
            hash.append(hexChars[i and 0x0f])
        }

        return hash.toString()

    }

}
