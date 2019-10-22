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
import androidx.core.view.isGone
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.tazkrtak.staff.R
import com.tazkrtak.staff.models.Account
import com.tazkrtak.staff.models.Conductor
import com.tazkrtak.staff.repositories.ConductorRepository
import com.tazkrtak.staff.util.Auth
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.view_qr.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScannerActivity : Activity(), DecoratedBarcodeView.TorchListener, CoroutineScope {

    private val REQUEST_Code = 0x11
    private var ticketPrices = arrayListOf<Double>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    lateinit var job: Job

    private val callback = object : BarcodeCallback {

        var lastText: String = ""

        override fun barcodeResult(result: BarcodeResult) {

            if (result.text == null || result.text == lastText)
                return

            lastText = result.text

            if (Auth.currentUser!!.type == Account.Type.CONDUCTOR) {

                launch {
                    val scanResult =
                        ConductorRepository.makeScan(lastText, getSelectedTicketPrice())

                    if (scanResult.isSuccess!!) {
                        qr_status_icon.setImageResource(R.drawable.ic_success)
                    } else {
                        qr_status_icon.setImageResource(R.drawable.ic_fail)
                    }
                    barcode_scanner.setStatusText(scanResult.message)
                }

            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        job = Job()

        if (Auth.currentUser!!.type == Account.Type.CONDUCTOR) {
            tickets_fees_picker.isGone = false
            tickets_fees_picker.isEnabled = true
            initFeesPicker()
        } else {
            tickets_fees_picker.isGone = true
            tickets_fees_picker.isEnabled = false
        }

        requestCameraPermission()

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        controlFlashlightButton()
        flashlight_button.setOnClickListener { controlFlashlight() }
        tickets_fees_picker.setOnValueChangedListener { _, _, _ -> callback.lastText = "" }

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

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
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

    /*
    * Ticket Fees Picker Functions
    */

    private fun initFeesPicker() {
        ticketPrices = (Auth.currentUser as Conductor).bus!!.ticketsPrices!!
        tickets_fees_picker.minValue = 0
        tickets_fees_picker.maxValue = ticketPrices.lastIndex
        tickets_fees_picker.displayedValues = ticketPrices.map {
            "$it ${getString(R.string.currency)}"
        }.toTypedArray()
    }

    private fun getSelectedTicketPrice(): Double {
        return ticketPrices[tickets_fees_picker.value]
    }

    /*
    * Camera Permission Functions
    */

    private fun requestCameraPermission() {
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
                            , REQUEST_Code
                        )
                    }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA)
                    , REQUEST_Code
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_Code) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }
}