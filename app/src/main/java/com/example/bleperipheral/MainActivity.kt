package com.example.bleperipheral

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.ScanResult
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
class MainActivity : AppCompatActivity() {
    private lateinit var mText:TextView
    private lateinit var mAdvertiseButton: Button
    private lateinit var mDiscoverButton: Button
    private var isAdvertising = false
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleAdvertiser by lazy {
        bluetoothAdapter.bluetoothLeAdvertiser
    }

    private val advertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!bluetoothAdapter.isMultipleAdvertisementSupported)
        {
            log("I suck")
        }
        mText = findViewById(R.id.text)
        mAdvertiseButton = findViewById(R.id.advertise_btn)
        mAdvertiseButton.setOnClickListener{if(!isAdvertising)advertise() else stopAdvertise()}
    }



    private fun advertise(){
        log("Yessss")
        mText.text = "MosipBlePeripheral"
        val pUuid : ParcelUuid = ParcelUuid(UUID.fromString(getString(R.string.ble_uuid)))
        val data : AdvertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceData(pUuid, "bt".toByteArray(Charsets.UTF_8))
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE),2)
        }
        bleAdvertiser.startAdvertising(advertiseSettings,data,advertiseCallback)
        isAdvertising = true
    }

    private fun stopAdvertise(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE),2)
        }
        bleAdvertiser.stopAdvertising(advertiseCallback)
        isAdvertising = false
    }

    private val advertiseCallback = object :AdvertiseCallback()
    {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
        }


        override fun onStartFailure(errorCode: Int) {
            log("Advertisiing failure"+errorCode)
            super.onStartFailure(errorCode)
        }


    }
    @SuppressLint("SetTextI18n")
    private fun log(message: String) {
        val formattedMessage = String.format("%s: %s", Date(), message)
        runOnUiThread {
            val currentLogText = if (log_text_view.text.isEmpty()) {
                "Beginning of log."
            } else {
                log_text_view.text
            }
            log_text_view.text = "$currentLogText\n$formattedMessage"
            log_scroll_view.post { log_scroll_view.fullScroll(View.FOCUS_DOWN) }
        }
    }
}