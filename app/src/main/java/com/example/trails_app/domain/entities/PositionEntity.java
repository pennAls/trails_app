package com.example.trails_app.domain.entities;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class PositionEntity {
    String positionId;
    String trailId;
    double latitude;
    double longitude;
    LocalDateTime timestamp;
    double instantaneousSpeed;
    float accuracy;
}