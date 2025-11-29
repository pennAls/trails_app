package com.example.trails_app.providers;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.listeners.OnLocationReceivedListener;

import java.util.Optional;


public interface LocationProvider {
    void startLocationUpdate(OnLocationReceivedListener onLocationReceivedListener) ;

    void stopLocationUpdate();

    void handlePermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
