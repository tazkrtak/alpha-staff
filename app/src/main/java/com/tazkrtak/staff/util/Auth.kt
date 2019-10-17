package com.tazkrtak.staff.util

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tazkrtak.staff.App
import com.tazkrtak.staff.R
import com.tazkrtak.staff.models.Account
import com.tazkrtak.staff.models.Collector
import com.tazkrtak.staff.models.Conductor
import kotlinx.coroutines.tasks.await

object Auth {

    private const val ID = "id"
    private const val PASSWORD = "password"

    var currentUser: Account? = null

    suspend fun isSignedIn(): Boolean {
        if (currentUser == null) {
            val data = getPrefs()
            if (!data[ID].isNullOrEmpty() && !data[PASSWORD].isNullOrEmpty()) {
                fetchData(data[ID].toString(), data[PASSWORD].toString())
                return true
            }
        }
        return false
    }

    suspend fun signIn(id: String, password: String) {

        if (id.isEmpty()) throw AuthIdException(getMessage(R.string.id_required))
        if (password.isEmpty()) throw AuthPasswordException(getMessage(R.string.password_required))

        val hashedPassword = Hash.sha512(password)
        fetchData(id, hashedPassword)
        setPrefs(id, hashedPassword)

    }

    fun signOut() {
        setPrefs(null, null)
        currentUser = null
    }

    private fun getAccountFromDocument(id: String, doc: DocumentSnapshot): Account? {
        return if (Account.typeOf(id) == Account.Type.CONDUCTOR) {
            doc.toObject(Conductor::class.java)
        } else {
            doc.toObject(Collector::class.java)
        }
    }

    private suspend fun fetchData(id: String, hashedPassword: String) {

        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("staff").document(id).get().await()

        if (!doc.exists()) throw AuthIdException(getMessage(R.string.id_error))
        val docPassword = doc.data?.get(PASSWORD)?.toString()
        if (docPassword != hashedPassword) throw AuthPasswordException(getMessage(R.string.password_error))

        currentUser = getAccountFromDocument(id, doc)
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

    private fun getMessage(id: Int): String {
        return App.appContext!!.getString(id)
    }

    abstract class AuthException(message: String) : Exception(message)
    class AuthIdException(message: String) : AuthException(message)
    class AuthPasswordException(message: String) : AuthException(message)

}
