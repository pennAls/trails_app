package com.example.trails_app.usecases;

import android.location.Location;

import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.domain.entities.TrailEntity;
import com.example.trails_app.repository.PositionRepository;
import com.example.trails_app.repository.TrailRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TrailOperationsUseCase {
    private final TrailRepository trailRepository;
    public TrailOperationsUseCase(TrailRepository trailRepository) {
        this.trailRepository = trailRepository;
    }
    public String startTrail() {
        String uuid = UUID.randomUUID().toString();
        TrailEntity trail = new TrailEntity(
                uuid,
                "Trilha " + LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                0.0,
                0.0,
                0.0
        );
        trailRepository.save(trail);
        return uuid;
    }
    public TrailEntity finishTrail(String trailId) {
        List<PositionEntity> positions = trailRepository.findPositionsByTrailId(trailId);

        if (positions == null || positions.isEmpty()) {
            return null;
        }

        double maxSpeed = 0.0;
        double totalDistanceMeters = 0.0;
        PositionEntity lastPos = null;

        for (PositionEntity pos : positions) {
            if (pos.getInstantaneousSpeed() > maxSpeed) {
                maxSpeed = pos.getInstantaneousSpeed();
            }

            if (lastPos != null) {
                float[] results = new float[1];
                Location.distanceBetween(
                        lastPos.getLatitude(), lastPos.getLongitude(),
                        pos.getLatitude(), pos.getLongitude(),
                        results
                );
                totalDistanceMeters += results[0];
            }
            lastPos = pos;
        }

        PositionEntity startPos = positions.get(0);
        PositionEntity endPos = positions.get(positions.size() - 1);

        LocalDateTime startTime = startPos.getTimestamp();
        LocalDateTime endTime = endPos.getTimestamp() != null ? endPos.getTimestamp() : LocalDateTime.now();

        long totalSeconds = Duration.between(startTime, endTime).getSeconds();
        double totalHours = totalSeconds / 3600.0;
        double totalDistanceKm = totalDistanceMeters / 1000.0;

        double averageSpeedKmh = (totalHours > 0) ? (totalDistanceKm / totalHours) : 0.0;
        double maxSpeedKmh = maxSpeed * 3.6;
        double calories = totalDistanceKm * 70.0 * 1.036;

        TrailEntity finishedTrail = new TrailEntity(
                trailId,
                "Trilha " + endTime.toLocalDate(),
                startTime,
                endTime,
                calories,
                averageSpeedKmh,
                maxSpeedKmh
        );

        trailRepository.updateTrail(finishedTrail);

        return finishedTrail;
    }
}