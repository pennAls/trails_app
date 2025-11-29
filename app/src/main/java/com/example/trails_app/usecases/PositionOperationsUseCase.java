package com.example.trails_app.usecases;
import com.example.trails_app.repository.PositionRepository;

import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.repository.TrailRepository;

public class PositionOperationsUseCase {
    private final PositionRepository positionRepository;

    public PositionOperationsUseCase(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;

    }

    public void savePosition(PositionEntity position, String trailId){
        positionRepository.save(position,trailId);
    }
}
