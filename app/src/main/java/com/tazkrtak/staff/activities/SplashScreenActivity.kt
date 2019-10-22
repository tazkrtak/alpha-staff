package com.tazkrtak.staff.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tazkrtak.staff.R
import com.tazkrtak.staff.util.Auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SplashScreenActivity : AppCompatActivity(), CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        launch {
            if (Auth.isSignedIn()) {
                launchActivity(MainActivity::class.java)
            } else {
                launchActivity(SignInActivity::class.java)
            }
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun launchActivity(cls: Class<out Any>) {
        val activityIntent = Intent(this, cls)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(activityIntent)
        finish()
    }
}
