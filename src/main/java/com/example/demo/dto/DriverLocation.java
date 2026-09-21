package com.example.demo.dto;

import java.time.Instant;

/** Broadcast payload pushed to subscribers of a driver's live position. */
public record DriverLocation(long driverId, double lon, double lat, Instant at) {}
