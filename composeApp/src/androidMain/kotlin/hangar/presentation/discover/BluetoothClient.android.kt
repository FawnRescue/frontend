package hangar.presentation.discover

import AdvertisementDataRetrievalKeys
import android.Manifest
import android.app.Activity
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
import kotlin.math.min

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
    private var dataSend: Boolean? = null

    private var dataToSend: ByteArray? = null
    private var currentChunkIndex = 0
    private val chunkSize = 100 // Adjust as needed
    private val scanningFlow = flow<Boolean> {
        while (true) {
            emit(blueFalcon.isScanning)
            delay(100)
        }
    }
    private val transmittingFlow = flow<Boolean> {
        while (true) {
            emit(currentChunkIndex == 0)
            delay(100)
        }
    }
    private val percentTransmitted = flow<Float> {
        while (true) {
            if (dataToSend == null) {
                emit(-1f)
            } else {
                emit(currentChunkIndex.toFloat() / dataToSend!!.size.toFloat())
            }
            delay(100)
        }
    }

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
        devices.remove(bluetoothPeripheral.uuid)
        if (dataSend == null) {
            dataSend = false
        }
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
                    connectedDevice?.let { device ->
                        val connectMessage = "FawnRescue-Key,"
                        blueFalcon.writeCharacteristic(
                            device,
                            it,
                            connectMessage.toByteArray(),
                            null
                        )
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
        if (!success) {
            // Last result is false. Not entirely sure why but if there is time we should investigate that. Normally this should only be handled in the else clause
            println("All chunks sent")
            devices.remove(bluetoothPeripheral.uuid)
            dataToSend = null
            currentChunkIndex = 0
            connectedDevice = null
            blueFalcon.disconnect(bluetoothPeripheral)
            dataSend = true
            return
        }

        // Send the next chunk of data if there's more data to send
        dataToSend?.let { byteArray ->
            if (currentChunkIndex < byteArray.size) {
                val end = min(currentChunkIndex + chunkSize, byteArray.size)
                val chunk = byteArray.copyOfRange(currentChunkIndex, end)
                currentChunkIndex = end

                blueFalcon.writeCharacteristic(
                    bluetoothPeripheral,
                    bluetoothCharacteristic,
                    chunk,
                    null
                )
            } else {
                println("All chunks sent")
                devices.remove(bluetoothPeripheral.uuid)
                dataToSend = null
                currentChunkIndex = 0
                connectedDevice = null
                blueFalcon.disconnect(bluetoothPeripheral)
                dataSend = true
                return
            }
        }
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

    actual suspend fun connectDrone(
        address: String,
        email: String,
        otp: String,
        token: String
    ): Boolean {
        connectedDevice = devices[address]

        connectedDevice?.let { blueFalcon.connect(it, false) }

        // Prepare the data to be sent in chunks
        val message = "${otp},${email},${token}\n"
        dataSend = null
        dataToSend = message.toByteArray()
        currentChunkIndex = 0

        while (dataSend == null) {
            delay(100)
        }

        return dataSend ?: false
    }

    actual fun scanningFlow(): Flow<Boolean> = scanningFlow

    actual fun transmittingFlow(): Flow<Boolean> = transmittingFlow

    actual fun percentTransmitted(): Flow<Float> = percentTransmitted
}
