package com.practica.conexionsensores.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.practica.conexionsensores.data.WifiClient
import com.practica.conexionsensores.data.BluetoothManager

class SensorViewModel(application: Application) : AndroidViewModel(application) {
    //private val wifiClient = WifiClient("192.168.80.29", 8080)
    private val bluetoothManager = BluetoothManager(application.applicationContext)
    private val _data = MutableStateFlow("Esperando datos...")
    val data: StateFlow<String> = _data

    fun connect(){
        viewModelScope.launch {
            //val connected = wifiClient.connect()
            val connected = bluetoothManager.connectTo("ALEJOPC1")
            if (connected) {
                _data.value = "Conectado al sensor"
                listen()
            } else {
                _data.value = "Error al conectar"
            }
        }
    }

    private fun listen() {
        viewModelScope.launch {
            while (true) {
                //val msg = wifiClient.readData()
                val msg = bluetoothManager.readData()
                msg?.let {_data.value = it}
            }
        }
    }

    override fun onCleared() {
        //wifiClient.disconnect()
        bluetoothManager.close()
        super.onCleared()
    }
}

