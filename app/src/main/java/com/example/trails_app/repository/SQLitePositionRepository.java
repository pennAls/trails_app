package com.example.trails_app.repository;

import com.example.trails_app.domain.entities.PositionEntity;

import java.util.Collections;
import java.util.List;

public class SQLitePositionRepository implements PositionRepository {
    @Override
    public void save(PositionEntity position) {

    }

    @Override
    public void saveAll(List<PositionEntity> positions) {

    }

    @Override
    public List<PositionEntity> findByTrailId(String trailId) {
        return Collections.emptyList();
    }
}
