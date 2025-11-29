package com.example.trails_app.listeners;

import com.example.trails_app.domain.entities.PositionEntity;
public interface OnLocationReceivedListener {
    void onLocationReceived(PositionEntity position);
}