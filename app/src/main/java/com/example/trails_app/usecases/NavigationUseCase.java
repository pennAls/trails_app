package com.example.trails_app.usecases;
import android.location.Location;
import com.example.trails_app.domain.entities.PositionEntity;

public class NavigationUseCase {
    private PositionEntity lastPosition;
    private float currentBearing = 0f;

    public float calculateBearing(PositionEntity currentPosition, int navMode) {
        if (navMode != 2) {
            return 0f;
        }

        if (lastPosition != null) {
            Location prev = new Location("");
            prev.setLatitude(lastPosition.getLatitude());
            prev.setLongitude(lastPosition.getLongitude());

            Location curr = new Location("");
            curr.setLatitude(currentPosition.getLatitude());
            curr.setLongitude(currentPosition.getLongitude());

            if (prev.distanceTo(curr) > 2.0) {
                currentBearing = prev.bearingTo(curr);
            }
        }
        lastPosition = currentPosition;
        return currentBearing;
    }

    public void reset() {
        lastPosition = null;
        currentBearing = 0f;
    }
}