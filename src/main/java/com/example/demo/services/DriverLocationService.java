package com.example.demo.services;

import java.time.Instant;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DriverLocation;
import com.example.demo.entities.Driver;
import com.example.demo.repositories.DriverGeoRepository;
import com.example.demo.repositories.DriverRepository;

/**
 * Handles a driver location ping: writes the hot position to Redis Geo,
 * refreshes the durable snapshot in Postgres, and broadcasts to subscribers
 * over STOMP so tracking users see movement instantly (no polling).
 */
@Service
public class DriverLocationService {

    public static final String TOPIC_PREFIX = "/topic/drivers/";

    private static final GeometryFactory GEO = new GeometryFactory(new PrecisionModel(), 4326);

    private final DriverGeoRepository driverGeoRepository;
    private final DriverRepository driverRepository;
    private final SimpMessagingTemplate messaging;

    public DriverLocationService(DriverGeoRepository driverGeoRepository,
                                 DriverRepository driverRepository,
                                 SimpMessagingTemplate messaging) {
        this.driverGeoRepository = driverGeoRepository;
        this.driverRepository = driverRepository;
        this.messaging = messaging;
    }

    public void updateLocation(long driverId, double lon, double lat) {
        Instant now = Instant.now();

        // 1. Hot path: high-throughput live position.
        driverGeoRepository.updateLocation(driverId, lon, lat);

        // 2. Durable snapshot (candidate for throttling under heavy load).
        driverRepository.findById(driverId).ifPresent(driver -> {
            driver.setLastLocation(GEO.createPoint(new Coordinate(lon, lat)));
            driver.setLastLocationAt(now);
            driverRepository.save(driver);
        });

        // 3. Push to every subscriber of this driver's topic.
        messaging.convertAndSend(TOPIC_PREFIX + driverId,
                new DriverLocation(driverId, lon, lat, now));
    }
}
