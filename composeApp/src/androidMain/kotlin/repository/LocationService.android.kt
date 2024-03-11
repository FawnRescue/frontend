package repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.maps.LatLong
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class LocationService : KoinComponent {

    private val context by inject<Context>()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission") // Ensure permissions are handled beforehand
    actual suspend fun getCurrentLocation(): LatLong? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(LatLong(location.latitude, location.longitude))
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    @SuppressLint("MissingPermission") // Ensure permissions are handled beforehand
    actual fun location(): Flow<LatLong> {

        return callbackFlow {
            val locationRequest = LocationRequest.create().apply {
                // Set your desired interval, priority, etc.
                interval = 5000
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        trySend(LatLong(location.latitude, location.longitude))
                    }
                }
            }

            // Assuming permissions are already handled:
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }
}