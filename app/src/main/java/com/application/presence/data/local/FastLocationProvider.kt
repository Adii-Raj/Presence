package com.application.presence.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider

class FastMyLocationProvider(context: Context) : IMyLocationProvider {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var consumer: IMyLocationConsumer? = null

    // This listens for Google's ultra-fast location updates
    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    // Feed the location directly into the Osmdroid blue dot
                    consumer?.onLocationChanged(location, this@FastMyLocationProvider)
                }
            }
        }
    }

    @SuppressLint("MissingPermission") // We handle permissions in our Compose UI before calling this
    override fun startLocationProvider(myLocationConsumer: IMyLocationConsumer?): Boolean {
        consumer = myLocationConsumer

        // Request high-accuracy updates every 3 seconds
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        return true
    }

    override fun stopLocationProvider() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        consumer = null
    }

    override fun getLastKnownLocation(): Location? = null

    override fun destroy() {
        stopLocationProvider()
    }
}