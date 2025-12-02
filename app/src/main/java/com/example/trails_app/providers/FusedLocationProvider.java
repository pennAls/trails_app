package com.example.trails_app.providers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.listeners.OnLocationReceivedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.time.LocalDateTime;

public class FusedLocationProvider implements LocationProvider {
    private final Activity activity;
    private final FusedLocationProviderClient fusedClient;
    private static final int REQUEST_LOCATION_UPDATES = 1;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private OnLocationReceivedListener listener;

    public FusedLocationProvider(Activity activity) {
        this.activity = activity;
        this.fusedClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @Override
    public void stopLocationUpdate() {
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void startLocationUpdate(OnLocationReceivedListener listener) {
        this.listener = listener;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            long timeInterval = 5 * 1000;
            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).build();

            locationCallback = new LocationCallback() {
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    var position = toPositionEntity(location);
                    if (listener != null) {
                        listener.onLocationReceived(position);
                    }
                }
            };

            fusedClient.requestLocationUpdates(locationRequest, locationCallback, null);

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        }
    }

    private PositionEntity toPositionEntity(Location location) {
        return new PositionEntity(
                null,
                null,
                location.getLatitude(),
                location.getLongitude(),
                LocalDateTime.now(),
                location.getSpeed(),
                location.getAccuracy()
        );
    }

    @Override
    public void handlePermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (this.listener != null) {
                    startLocationUpdate(this.listener);
                }
            }
        } else {
            Toast.makeText(activity, "Sem permissão para mostrar informações do sistema GNSS", Toast.LENGTH_SHORT).show();
        }

    }
}