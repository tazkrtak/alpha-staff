package com.tazkrtak.staff.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.zxing.integration.android.IntentIntegrator
import com.tazkrtak.staff.R
import com.tazkrtak.staff.models.Account
import com.tazkrtak.staff.models.Conductor
import com.tazkrtak.staff.repositories.TransactionRepository
import com.tazkrtak.staff.util.Auth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        var ticketsCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        account_name_text_view.text = Auth.currentUser!!.name
        if (Auth.currentUser!!.type == Account.Type.CONDUCTOR) {
            bus_information_text_view.text =
                (Auth.currentUser!! as Conductor).bus!!.number.toString()
            launch {
                if (ticketsCount != 0) return@launch
                ticketsCount = TransactionRepository.getCountOfToday()
                tickets_count_text_view.text = ticketsCount.toString()
            }
        } else {
            card_divider.isGone = true
            bus_information_text_view.isGone = true
            tickets_linear_view.isGone = true
        }

        scan_button.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.captureActivity = ScannerActivity::class.java
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setBeepEnabled(false)
            integrator.initiateScan()
        }

        bottom_app_bar.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.sign_out) -> {
                    Auth.signOut()
                    val signInActivityIntent = Intent(this, SignInActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(signInActivityIntent)
                    finish()
                    return@setOnMenuItemClickListener true
                }
                getString(R.string.help) -> {
                    return@setOnMenuItemClickListener true
                }
                else -> false

            }
        }

    }

    override fun onResume() {
        tickets_count_text_view.text = ticketsCount.toString()
        super.onResume()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}