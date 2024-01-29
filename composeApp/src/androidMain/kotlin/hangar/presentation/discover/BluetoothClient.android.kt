package hangar.presentation.discover

import AdvertisementDataRetrievalKeys
import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import dev.bluefalcon.BlueFalcon
import dev.bluefalcon.BlueFalconDelegate
import dev.bluefalcon.BluetoothCharacteristic
import dev.bluefalcon.BluetoothCharacteristicDescriptor
import dev.bluefalcon.BluetoothPeripheral
import di.AndroidApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

actual class BluetoothClient : KoinComponent, BlueFalconDelegate {
    private val context: Context by inject()
    private val application: AndroidApplication by inject()
    private val blueFalcon = BlueFalcon(application.context, null)
    private var connectedDevice: BluetoothPeripheral? = null
    private var devices: MutableMap<String, BluetoothPeripheral> = mutableMapOf()
    private var devicesFlow: Flow<List<BluetoothDevice>> = flow {
        while (true) {
            emit(devices.map {
                BluetoothDevice(
                    it.value.name ?: "Unknown", it.key
                )
            })
            delay(100)
        }
    }
    private var dataSend = false
    private var token: String? = null
    private var key: String? = null

    @RequiresApi(Build.VERSION_CODES.S)
    actual fun startScan() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                0
            )
            return
        }
        println("Start Scan")
        blueFalcon.delegates.add(this)
        blueFalcon.scan()
    }

    actual fun stopScan() {
        connectedDevice?.let {
            blueFalcon.disconnect(it)
            connectedDevice = null
        }
        blueFalcon.stopScanning()
    }

    override fun didCharacteristcValueChanged(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic
    ) {
        println()
    }

    override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {
        println("Connected")
    }

    override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {
        println("Disconnected!")
        dataSend = false
        key = null
        token = null
    }

    override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {
        println("Found Characteristic")
    }

    override fun didDiscoverDevice(
        bluetoothPeripheral: BluetoothPeripheral,
        advertisementData: Map<AdvertisementDataRetrievalKeys, Any>
    ) {
        if (bluetoothPeripheral.name == "FawnRescue") {
            devices[bluetoothPeripheral.uuid] = bluetoothPeripheral
        }
    }

    override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {
        println("Found Services!")
        println("Services: ${bluetoothPeripheral.services.size}")
        bluetoothPeripheral.services.forEach { service ->
            service.characteristics.forEach {
                println(
                    it.characteristic.uuid.toString()

                )
                if (it.characteristic.uuid == UUID.fromString("502e4974-fc68-42b1-8402-33daf244e47c")) {
                    println("Write Data")
                    connectedDevice?.let { it1 ->
                        blueFalcon.writeCharacteristic(
                            it1,
                            it,
                            "${token},${key}", null
                        )
                        println("Send Data")
                        dataSend = true
                    }
                }
            }
        }
    }

    override fun didReadDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
        println("Read Descriptor")
    }

    override fun didRssiUpdate(bluetoothPeripheral: BluetoothPeripheral) {
        println("Update RSSI")
    }

    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {
        println("Update MTU")
    }

    override fun didWriteCharacteristic(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic,
        success: Boolean
    ) {
        println("Write Characteristic")
    }

    override fun didWriteDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
        println("Write Characteristic")
    }

    actual fun getDrones(): Flow<List<BluetoothDevice>> {
        return devicesFlow
    }

    actual suspend fun connectDrone(address: String, key: String, token: String): Boolean {
        this.key = key
        this.token = token
        connectedDevice = devices[address]
        connectedDevice?.let { blueFalcon.connect(it, false) }
        while (!dataSend) {
            delay(100)
        }
        //TODO add timeout
        return true
    }

    actual fun isScanning(): Boolean {
        return blueFalcon.isScanning
    }

}
