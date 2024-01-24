package hangar.presentation.discover

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import org.fawnrescue.project.MainActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

actual class BluetoothServer : KoinComponent {
    private val context: Context by inject()
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private var bluetoothGattServer: BluetoothGattServer? = null

    @RequiresApi(Build.VERSION_CODES.S)
    actual fun startServer() {
        println("Start Bluettoth")
        println(SERVICE_UUID)
        println(CHARACTERISTIC_UUID)
        println(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        )
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                0
            )
            return
        }
        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback)
        setupServer()
        startAdvertising()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupServer() {
        val service = BluetoothGattService(
            SERVICE_UUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        // Add characteristics, for example a read characteristic
        val readCharacteristic = BluetoothGattCharacteristic(
            CHARACTERISTIC_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        service.addCharacteristic(readCharacteristic)

        // Add more characteristics as needed

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                0
            )
            return
        }
        bluetoothGattServer?.addService(service)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startAdvertising() {
        val advertiser: BluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE),
                0
            )
            return
        }
        advertiser.startAdvertising(settings, data, advertiseCallback)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    actual fun stopServer() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                0
            )
            return
        }
        bluetoothGattServer?.close()
        // Stop advertising if needed
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                device?.let {
                    println("Device connected: ${it.address}")
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                device?.let {
                    println("Device disconnected: ${it.address}")
                }
            }
        }
    }
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            println("Success Connect")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            println("Failed Connect")
        }
    }
}

val SERVICE_UUID: UUID = UUID.fromString("c32a2876-a31c-4cf0-a97d-1f003d91ebf8")
val CHARACTERISTIC_UUID: UUID = UUID.fromString("362b5dee-7a49-4438-8507-91483a32d5d3")