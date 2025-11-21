package com.example.trails_app.repository;
import com.example.trails_app.domain.entities.PositionEntity;

import java.util.List;

public interface PositionRepository {
    void save(PositionEntity position);
    void saveAll(List<PositionEntity> positions);
}
