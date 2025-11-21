package com.example.trails_app.domain.entities;
import java.time.LocalDateTime;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class TrailEntity {
    String trailId;
    String name;
    LocalDateTime beginning;
    LocalDateTime ending;
    double caloricExpenditure;
    double averageSpeed;
    double maximumSpeed;
}
