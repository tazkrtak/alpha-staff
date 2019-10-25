package com.tazkrtak.staff.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.zxing.integration.android.IntentIntegrator
import com.tazkrtak.staff.R
import com.tazkrtak.staff.models.Account
import com.tazkrtak.staff.models.Conductor
import com.tazkrtak.staff.util.Auth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        account_name_text_view.text = Auth.currentUser!!.name

        if (Auth.currentUser!!.type == Account.Type.CONDUCTOR) {
            information_text_view.text = (Auth.currentUser!! as Conductor).bus!!.number.toString()
        } else {
            card_divider.isGone = true
            information_text_view.isGone = true
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
}