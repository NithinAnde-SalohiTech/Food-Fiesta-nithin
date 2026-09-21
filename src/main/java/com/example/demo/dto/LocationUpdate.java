package com.example.demo.dto;

/** Inbound driver location ping (lon/lat, WGS84). */
public record LocationUpdate(double lon, double lat) {}
