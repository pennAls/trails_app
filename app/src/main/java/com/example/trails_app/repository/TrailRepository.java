package com.example.trails_app.repository;
import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.domain.entities.TrailEntity;

import java.util.List;
import java.util.Optional;

public interface TrailRepository {
    void save(TrailEntity trail);
    Integer findById(String id);
    List<TrailEntity> findAll();
    void deleteById(String id);
    List<PositionEntity> findPositionsByTrailId(String trailId);
    void updateTrail(TrailEntity finishedTrail);
}
