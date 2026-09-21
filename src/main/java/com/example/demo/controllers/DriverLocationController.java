package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LocationUpdate;
import com.example.demo.services.DriverLocationService;

/**
 * Two ways for a driver client to push its position:
 *  - REST: POST /api/drivers/{driverId}/location  (simple simulators / mobile)
 *  - STOMP: SEND /app/drivers/{driverId}/location  (persistent socket)
 * Both fan out to subscribers of /topic/drivers/{driverId}.
 */
@RestController
@RequestMapping("/api/drivers")
public class DriverLocationController {

    private final DriverLocationService driverLocationService;

    public DriverLocationController(DriverLocationService driverLocationService) {
        this.driverLocationService = driverLocationService;
    }

    @PostMapping("/{driverId}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable long driverId,
                                               @RequestBody LocationUpdate update) {
        driverLocationService.updateLocation(driverId, update.lon(), update.lat());
        return ResponseEntity.accepted().build();
    }

    @MessageMapping("/drivers/{driverId}/location")
    public void onLocation(@DestinationVariable long driverId,
                           @Payload LocationUpdate update) {
        driverLocationService.updateLocation(driverId, update.lon(), update.lat());
    }
}
