package com.example.trails_app.TrailManager;
import android.graphics.Color;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrailRenderer {
    private final GoogleMap mMap;
    private Marker userMarker;
    private Circle accuracyCircle;

    public TrailRenderer(GoogleMap googleMap) {
        this.mMap = googleMap;
    }
    public void updatePosition(LatLng position, float accuracy) {
        if (mMap == null) return;

        if (userMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title("Eu");
            userMarker = mMap.addMarker(markerOptions);
        } else {
            userMarker.setPosition(position);
        }

        if (accuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(position)
                    .radius(accuracy)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(3)
                    .fillColor(Color.TRANSPARENT);
            accuracyCircle = mMap.addCircle(circleOptions);
        } else {
            accuracyCircle.setCenter(position);
            accuracyCircle.setRadius(accuracy);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f));
    }

    public void reset() {
        if (userMarker != null) {
            userMarker.remove();
            userMarker = null;
        }
        if (accuracyCircle != null) {
            accuracyCircle.remove();
            accuracyCircle = null;
        }
    }
}