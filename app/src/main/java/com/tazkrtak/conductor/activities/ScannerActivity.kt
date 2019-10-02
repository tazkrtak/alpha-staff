package com.tazkrtak.conductor.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.tazkrtak.conductor.R
import kotlinx.android.synthetic.main.activity_scanner.*

class ScannerActivity : Activity(), DecoratedBarcodeView.TorchListener {

    private var lastText: String = ""

    private val callback = object : BarcodeCallback {

        override fun barcodeResult(result: BarcodeResult) {
            if (result.text != null && result.text != lastText) {
                lastText = result.text
                // TODO: Implement what happens after a QR code is scanned.
                barcode_scanner.setStatusText(result.text)
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

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
        if (getString(R.string.turn_on_flashlight) == flashlight_button.text) {
            barcode_scanner.setTorchOn()
        } else {
            barcode_scanner.setTorchOff()
        }
    }

    override fun onTorchOn() {
        flashlight_button.setText(R.string.turn_off_flashlight)
    }

    override fun onTorchOff() {
        flashlight_button.setText(R.string.turn_on_flashlight)
    }

}