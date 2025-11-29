package com.example.trails_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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

    private TrailRenderer trailRenderer;
    private TrailStatsManager trailStatsManager;

    private String currentTrailId;
    private boolean isActive = false;

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

        locationProvider = new FusedLocationProvider(this);
        trailStatsManager = new TrailStatsManager(binding);

        binding.btnStartTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActive) {
                    currentTrailId = trailOperationsUseCase.startTrail();
                    if (trailRenderer != null) trailRenderer.reset();
                    trailStatsManager.start();
                    isActive = true;

                    locationProvider.startLocationUpdate(position -> {
                        positionOperationsUseCase.savePosition(position, currentTrailId);
                        trailStatsManager.update(position);

                        if (trailRenderer != null) {
                            LatLng newPosition = new LatLng(position.getLatitude(), position.getLongitude());
                            trailRenderer.updatePosition(newPosition, position.getAccuracy());
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
                    trailOperationsUseCase.finishTrail(currentTrailId);
                    isActive = false;
                    currentTrailId = null;
                    if (trailRenderer != null) trailRenderer.reset();
                    trailStatsManager.reset();
                    Toast.makeText(MapsActivity.this, "Trilha Finalizada", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        trailRenderer = new TrailRenderer(mMap);

        LatLng startPos = new LatLng(-12.94825, -38.41334);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPos, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationProvider.handlePermissionsResult(requestCode, permissions, grantResults);
    }
}