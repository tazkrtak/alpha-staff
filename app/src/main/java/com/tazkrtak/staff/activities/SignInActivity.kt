package com.tazkrtak.staff.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tazkrtak.staff.R
import com.tazkrtak.staff.util.Auth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SignInActivity : AppCompatActivity(), CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_in_button.setOnClickListener {

            id_edit_text.error = null
            id_edit_text.error = null

            val id = id_edit_text.text.toString()
            val password = password_edit_text.text.toString()

            launch {
                try {
                    Auth.signIn(id, password)
                    launchMainActivity()
                } catch (e: Auth.AuthIdException) {
                    id_edit_text.error = e.message
                } catch (e: Auth.AuthPasswordException) {
                    password_edit_text.error = e.message
                }
            }

        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun launchMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainActivityIntent)
        finish()
    }

}

