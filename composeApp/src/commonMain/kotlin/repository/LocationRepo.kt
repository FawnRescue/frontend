package repository

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.maps.LatLong

class LocationRepo : KoinComponent {
    private val service by inject<LocationService>()
    private val permissionsController by inject<PermissionsController>()
    suspend fun getLocation(): Flow<LatLong> {
        try {
            permissionsController.providePermission(Permission.LOCATION)
            // Permission has been granted successfully.
        } catch (deniedAlways: DeniedAlwaysException) {
            // Permission is always denied.
        } catch (denied: DeniedException) {
            // Permission was denied.
        }
        return service.location()
    }
}