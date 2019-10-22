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
import com.tazkrtak.staff.models.TaskResult
import com.tazkrtak.staff.models.Ticket
import com.tazkrtak.staff.util.Auth
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.view_qr.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScannerActivity : Activity(), DecoratedBarcodeView.TorchListener, CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {

        requestCameraPermission()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        controlFeesPicker()
        controlFlashlightButton()
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
     * Scanning Logic
     */

    private val callback = object : BarcodeCallback {

        private var lastText: String = ""

        fun clearLast() {
            lastText = ""
        }

        override fun barcodeResult(result: BarcodeResult) {

            if (result.text == null || result.text == lastText) return
            lastText = result.text

            launch {
                val ticket = Ticket(result.text)
                val taskResult =
                    if (!ticket.isValid) {
                        TaskResult(R.string.ticket_validation_error)
                    } else Auth.currentUser?.executeTask(
                        ticket,
                        mapOf(
                            Conductor.REQUESTED_PRICE to getSelectedTicketPrice()
                        )
                    ) ?: return@launch

                barcode_scanner.setStatusText(taskResult.message)
                qr_status_icon.setImageResource(
                    if (taskResult.isSuccess) R.drawable.ic_success
                    else R.drawable.ic_fail
                )
            }

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}

    }


    /*
    * Fees Picker Functions
    */

    private fun controlFeesPicker() {
        if (Auth.currentUser!!.type == Account.Type.CONDUCTOR) {
            price_picker.isGone = false
            price_picker.isEnabled = true
            initFeesPicker()
            price_picker.setOnValueChangedListener { _, _, _ -> callback.clearLast() }
        } else {
            price_picker.isGone = true
            price_picker.isEnabled = false
        }
    }

    private fun initFeesPicker() {
        val conductor = Auth.currentUser as Conductor
        val prices = conductor.bus?.ticketsPrices!!
        price_picker.minValue = 0
        price_picker.maxValue = prices.lastIndex
        price_picker.displayedValues = prices.map {
            "$it ${getString(R.string.currency)}"
        }.toTypedArray()
    }

    private fun getSelectedTicketPrice(): Double {
        val conductor = Auth.currentUser as Conductor
        val prices = conductor.bus?.ticketsPrices!!
        return prices[price_picker.value]
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
        } else {
            flashlight_button.setOnClickListener {
                if (flashlight_button.alpha == 0.5F) {
                    barcode_scanner.setTorchOn()
                } else {
                    barcode_scanner.setTorchOff()
                }
            }
        }

    }

    override fun onTorchOn() {
        flashlight_button.alpha = 1F
    }

    override fun onTorchOff() {
        flashlight_button.alpha = 0.5F
    }


    /*
    * Camera Permission Functions
    */

    private val PERMESSION = Manifest.permission.CAMERA
    private val REQUEST_CODE = 0x11

    private fun requestCameraPermission() {
        val result = ContextCompat.checkSelfPermission(this, PERMESSION)
        if (result == PackageManager.PERMISSION_GRANTED) return
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMESSION)) {
            showPermissionDialog()
        } else {
            sendPermissionRequest()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_needed))
            .setMessage(getString(R.string.permission))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                sendPermissionRequest()
            }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    private fun sendPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(PERMESSION), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_CODE) return
        val message = if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
            getString(R.string.permission_granted)
        } else {
            getString(R.string.permission_denied)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}