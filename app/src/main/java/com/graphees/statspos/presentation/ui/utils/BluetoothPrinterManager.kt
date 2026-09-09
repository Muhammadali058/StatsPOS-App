package com.graphees.statspos.presentation.ui.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.UUID

class BluetoothPrinterManager {

    companion object {
        private const val TAG = "TAG"
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    private val sppUuid: UUID =
        UUID.fromString(
            "00001101-0000-1000-8000-00805F9B34FB"
        )

    @SuppressLint("MissingPermission")
    fun getPairedPrinters(): List<BluetoothDevice> {

        return try {

            val devices =
                bluetoothAdapter
                    ?.bondedDevices
                    ?.toList()
                    ?.sortedBy { it.name ?: "" }
                    ?: emptyList()

            Log.d(TAG, "Paired printers: ${devices.size}")

            devices.forEach { device ->

                Log.d(
                    TAG,
                    "Device: name=${device.name}, address=${device.address}, bondState=${device.bondState}"
                )
            }

            devices

        } catch (e: Exception) {

            Log.e(
                TAG,
                "getPairedPrinters ERROR: ${e.message}",
                e
            )

            emptyList()
        }
    }
    @SuppressLint("MissingPermission")
    suspend fun connect(
        device: BluetoothDevice
    ): Boolean = withContext(Dispatchers.IO) {

        try {

//            Log.d(TAG, "--------------------------------")
//            Log.d(TAG, "Connecting to printer")
//            Log.d(TAG, "Name: ${device.name}")
//            Log.d(TAG, "Address: ${device.address}")
//            Log.d(TAG, "Bond state: ${device.bondState}")

            disconnect()

//            Log.d(TAG, "Creating RFCOMM socket...")

            bluetoothSocket =
                device.createRfcommSocketToServiceRecord(
                    sppUuid
                )

//            Log.d(TAG, "Socket created")
//
//            Log.d(TAG, "Calling socket.connect()...")

            bluetoothSocket?.connect()

//            Log.d(TAG, "Bluetooth socket CONNECTED")

            outputStream =
                bluetoothSocket?.outputStream

            if (outputStream == null) {

//                Log.e(
//                    TAG,
//                    "OutputStream is NULL"
//                )

                disconnect()

                return@withContext false
            }

//            Log.d(
//                TAG,
//                "OutputStream obtained successfully"
//            )

            Log.d(TAG, "Printer connected successfully")
//            Log.d(TAG, "--------------------------------")

            true

        } catch (e: SecurityException) {

            Log.e(
                TAG,
                "BLUETOOTH PERMISSION ERROR: ${e.message}",
                e
            )

            disconnect()

            false

        } catch (e: Exception) {

            Log.e(
                TAG,
                "BLUETOOTH CONNECTION ERROR: ${e.javaClass.name}",
                e
            )

            Log.e(
                TAG,
                "Message: ${e.message}",
                e
            )

            disconnect()

            false
        }
    }

    suspend fun print(
        data: ByteArray
    ): Boolean = withContext(Dispatchers.IO) {

        try {

            val stream =
                outputStream
                    ?: run {

                        Log.e(
                            TAG,
                            "PRINT ERROR: OutputStream is NULL"
                        )

                        return@withContext false
                    }

//            Log.d(
//                TAG,
//                "Sending ${data.size} bytes to printer..."
//            )

            stream.write(data)

            stream.flush()
//
//            Log.d(
//                TAG,
//                "Data sent successfully"
//            )

            true

        } catch (e: Exception) {

            Log.e(
                TAG,
                "PRINT ERROR: ${e.javaClass.name}",
                e
            )

            Log.e(
                TAG,
                "Message: ${e.message}"
            )

            false
        }
    }

    fun isConnected(): Boolean {

        val connected =
            bluetoothSocket?.isConnected == true

//        Log.d(
//            TAG,
//            "isConnected = $connected"
//        )

        return connected
    }

    fun disconnect() {

//        Log.d(TAG, "Disconnecting printer...")

        try {
            outputStream?.close()
        } catch (e: Exception) {
            Log.e(TAG, "OutputStream close error", e)
        }

        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Socket close error", e)
        }

        outputStream = null
        bluetoothSocket = null

        Log.d(TAG, "Printer disconnected")
    }
}