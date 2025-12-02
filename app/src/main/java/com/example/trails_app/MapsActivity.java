package com.example.trails_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.trails_app.TrailManager.TrailRenderer;
import com.example.trails_app.TrailManager.TrailStatsManager;
import com.example.trails_app.repository.PositionRepository;
import com.example.trails_app.providers.FusedLocationProvider;
import com.example.trails_app.providers.LocationProvider;
import com.example.trails_app.repository.SQLitePositionRepository;
import com.example.trails_app.repository.SQLiteTrailRepository;
import com.example.trails_app.repository.TrailRepository;
import com.example.trails_app.usecases.NavigationUseCase;
import com.example.trails_app.usecases.PositionOperationsUseCase;
import com.example.trails_app.usecases.TrailOperationsUseCase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.trails_app.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private LocationProvider locationProvider;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private TrailOperationsUseCase trailOperationsUseCase;
    private PositionOperationsUseCase positionOperationsUseCase;
    private NavigationUseCase navigationUseCase;

    private TrailRenderer trailRenderer;
    private TrailStatsManager trailStatsManager;

    private String currentTrailId;
    private boolean isActive = false;
    private int mapTypePref = 1;
    private int navModePref = 1;
    private String realWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AppDatabaseHelper dbHelper = new AppDatabaseHelper(this);
        TrailRepository trailRepository = new SQLiteTrailRepository(dbHelper);
        PositionRepository positionRepository = new SQLitePositionRepository(dbHelper);

        trailOperationsUseCase = new TrailOperationsUseCase(trailRepository);
        positionOperationsUseCase = new PositionOperationsUseCase(positionRepository);
        navigationUseCase = new NavigationUseCase();

        locationProvider = new FusedLocationProvider(this);
        trailStatsManager = new TrailStatsManager(binding);

        binding.btnStartTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActive) {
                    currentTrailId = trailOperationsUseCase.startTrail();

                    if (trailRenderer != null) trailRenderer.reset();
                    trailStatsManager.start();
                    navigationUseCase.reset();

                    isActive = true;

                    locationProvider.startLocationUpdate(position -> {
                        positionOperationsUseCase.savePosition(position, currentTrailId);
                        trailStatsManager.update(position);

                        if (trailRenderer != null) {
                            LatLng pos = new LatLng(position.getLatitude(), position.getLongitude());
                            float bearing = navigationUseCase.calculateBearing(position, navModePref);
                            trailRenderer.updatePosition(pos, position.getAccuracy(), bearing, navModePref);
                        }
                    });
                } else {
                    Toast.makeText(MapsActivity.this, "Já existe uma trilha em andamento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnEndTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive) {
                    locationProvider.stopLocationUpdate();
                    double weight = Double.parseDouble(realWeight.replace(",", "."));
                    trailOperationsUseCase.finishTrail(currentTrailId, weight);

                    isActive = false;
                    currentTrailId = null;

                    if (trailRenderer != null) trailRenderer.reset();
                    trailStatsManager.reset();
                    navigationUseCase.reset();

                    Toast.makeText(MapsActivity.this, "Trilha Finalizada", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        trailRenderer = new TrailRenderer(mMap);
        loadPreferences();
        LatLng startPos = new LatLng(-12.94825, -38.41334);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPos, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationProvider.handlePermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserConfigs", Context.MODE_PRIVATE);
        mapTypePref = prefs.getInt("mapType", 1);
        navModePref = prefs.getInt("navMode", 1);
        realWeight = prefs.getString("weight", null);

        if (mMap != null) {
            if (mapTypePref == 2) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }
}