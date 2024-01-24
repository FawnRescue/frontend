package hangar.presentation.discover

import org.koin.core.component.KoinComponent

expect class BluetoothServer : KoinComponent {
    fun startServer()
    fun stopServer()
}