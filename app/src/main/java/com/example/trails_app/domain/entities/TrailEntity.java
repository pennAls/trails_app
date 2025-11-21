package com.example.trails_app.domain.entities;
import java.time.LocalDateTime;
import lombok.Value;

@Value
public class TrailEntity {
    String trailId;
    String name;
    LocalDateTime beginning;
    LocalDateTime end;
    double caloric_expenditure;
    double average_speed;
    double maximum_speed;
}
