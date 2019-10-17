package com.tazkrtak.staff.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.tazkrtak.staff.R
import com.tazkrtak.staff.util.Auth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scan_button.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.captureActivity = ScannerActivity::class.java
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setBeepEnabled(false)
            integrator.initiateScan()
        }

        sign_out_button.setOnClickListener {
            Auth.signOut()
            val signInActivityIntent = Intent(this, SignInActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(signInActivityIntent)
            finish()
        }

    }
}