package hangar.presentation.discover

import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

data class BluetoothDevice(val name: String, val address: String)
expect class BluetoothClient : KoinComponent {
    fun startScan()
    fun stopScan()
    fun getDrones(): Flow<List<BluetoothDevice>>
    fun scanningFlow(): Flow<Boolean>
    fun transmittingFlow(): Flow<Boolean>
    fun percentTransmitted(): Flow<Float>
    suspend fun connectDrone(
        address: String,
        refreshToken: String,
        accessToken: String,
        droneID: String
    ): Boolean
}