package com.practica.conexionsensores.data

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class BluetoothManager(private val context: Context) {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null

    suspend fun connectTo(deviceName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // ⚠️ Verificamos permiso antes de acceder al adaptador
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                throw SecurityException("Permiso BLUETOOTH_CONNECT no concedido")
            }

            val device: BluetoothDevice? =
                adapter?.bondedDevices?.find { it.name == deviceName }

            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            socket = device?.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun readData(): String? = withContext(Dispatchers.IO) {
        try {
            val input = socket?.inputStream ?: return@withContext null
            val buffer = ByteArray(1024)
            val bytes = input.read(buffer)
            String(buffer, 0, bytes)
        } catch (e: IOException) {
            null
        }
    }

    fun close() {
        try {
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}