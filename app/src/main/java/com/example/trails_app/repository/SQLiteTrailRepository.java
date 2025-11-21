package com.example.trails_app.repository;
import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.domain.entities.TrailEntity;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // Exemplo

public class SQLiteTrailRepository  implements TrailRepository {

    @Override
    public void save(TrailEntity trail) {

    }

    @Override
    public void findById(String id) {

    }

    @Override
    public List<TrailEntity> findAll() {
        return new ArrayList<>();
    }

    @Override
    public void deleteById(String id) {
    }

    @Override
    public List<PositionEntity> findPositionsByTrailId(String trailId) {
        return new ArrayList<>();
    }
}
