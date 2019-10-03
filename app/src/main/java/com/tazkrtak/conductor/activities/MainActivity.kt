package com.tazkrtak.conductor.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.tazkrtak.conductor.R
import com.tazkrtak.conductor.util.SharedPrefHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val savedId = SharedPrefHelper.getString(this, "busId")
        val password = SharedPrefHelper.getString(this, "password")

        val db = FirebaseFirestore.getInstance()
        db.collection("buses")
            .document(savedId)
            .get()
            .addOnSuccessListener {
                if (!it.exists() || it.data!!["password"] != password) {
                    SharedPrefHelper.removeString(this, "busId")
                    SharedPrefHelper.removeString(this, "password")
                    startActivity(intent)
                    finish()
                    return@addOnSuccessListener
                }

                scan_button.setOnClickListener {
                    val integrator = IntentIntegrator(this)
                    integrator.captureActivity = ScannerActivity::class.java
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    integrator.setBeepEnabled(false)
                    integrator.initiateScan()
                }

            }
    }
}