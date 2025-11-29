package com.example.trails_app.TrailManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import com.example.trails_app.databinding.ActivityMapsBinding;
import com.example.trails_app.domain.entities.PositionEntity;
import java.util.Locale;

public class TrailStatsManager {
    private final ActivityMapsBinding binding;
    private double maxSpeed = 0.0;
    private double totalDistance = 0.0;
    private long startTime = 0L;
    private PositionEntity lastPosition = null;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    public TrailStatsManager(ActivityMapsBinding binding) {
        this.binding = binding;
    }

    public void start() {
        reset();
        startTime = System.currentTimeMillis();
        startTimerLoop();
    }
    private void startTimerLoop() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int hours = minutes / 60;
                minutes = minutes % 60;

                if (hours > 0) {
                    binding.trailTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    binding.trailTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }
    public void update(PositionEntity position) {
        double currentSpeedKmh = position.getInstantaneousSpeed() * 3.6;

        if (currentSpeedKmh > maxSpeed) {
            maxSpeed = currentSpeedKmh;
        }

        if (lastPosition != null) {
            float[] results = new float[1];
            Location.distanceBetween(
                    lastPosition.getLatitude(), lastPosition.getLongitude(),
                    position.getLatitude(), position.getLongitude(),
                    results
            );
            totalDistance += (results[0] / 1000.0);
        }
        lastPosition = position;

        double calories = totalDistance * 70 * 1.036;

        binding.trailSpeed.setText(String.format(Locale.getDefault(), "%.1f km/h", currentSpeedKmh));
        binding.trailMaxSpeed.setText(String.format(Locale.getDefault(), "Máx: %.1f km/h", maxSpeed));
        binding.trailDistance.setText(String.format(Locale.getDefault(), "Dist: %.2f km", totalDistance));
        binding.trailCalories.setText(String.format(Locale.getDefault(), "🔥 %.0f kcal", calories));
    }

    public void reset() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        maxSpeed = 0.0;
        totalDistance = 0.0;
        startTime = 0L;
        lastPosition = null;
        binding.trailSpeed.setText("0.0 km/h");
        binding.trailMaxSpeed.setText("Máx: 0.0 km/h");
        binding.trailDistance.setText("Dist: 0.00 km");
        binding.trailTimer.setText("00:00");
        binding.trailCalories.setText("🔥 0 kcal");
    }
}