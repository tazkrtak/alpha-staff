package com.tazkrtak.staff.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.tazkrtak.staff.R
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.view_qr.*

class ScannerActivity : Activity(), DecoratedBarcodeView.TorchListener {

    private var lastText: String = ""
    private val MY_PERMISSIONS_REQUEST_CAMERA = 1

    private val callback = object : BarcodeCallback {

        override fun barcodeResult(result: BarcodeResult) {
            if (result.text != null && result.text != lastText) {
                lastText = result.text
                // TODO: Implement what happens after a QR code is scanned.
                qr_status_icon.setImageResource(R.drawable.ic_success)
                barcode_scanner.setStatusText(result.text)
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_needed))
                    .setMessage(getString(R.string.permission))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.CAMERA)
                            , MY_PERMISSIONS_REQUEST_CAMERA
                        )
                    }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA)
                    , MY_PERMISSIONS_REQUEST_CAMERA
                )
            }
        }

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        controlFlashlightButton()
        flashlight_button.setOnClickListener { controlFlashlight() }

        barcode_scanner.setTorchListener(this)
        barcode_scanner.decodeContinuous(callback)
    }

    override fun onResume() {
        super.onResume()
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcode_scanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


    /*
     * Flashlight Functions
     */

    private fun controlFlashlightButton() {
        // If the device does not have flashlight in its camera,
        // then remove the switch flashlight button.
        val hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        if (!hasFlash) {
            flashlight_button.visibility = View.GONE
        }
    }

    private fun controlFlashlight() {
        if (flashlight_button.alpha == 0.5F) {
            barcode_scanner.setTorchOn()
        } else {
            barcode_scanner.setTorchOff()
        }
    }

    override fun onTorchOn() {
        flashlight_button.alpha = 1F
    }

    override fun onTorchOff() {
        flashlight_button.alpha = 0.5F
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }
}