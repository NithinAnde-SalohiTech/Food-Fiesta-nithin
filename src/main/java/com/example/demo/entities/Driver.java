package com.example.demo.entities;

import java.time.Instant;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "driver")
public class Driver {

    public enum Status { OFFLINE, AVAILABLE, ASSIGNED, DELIVERING }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OFFLINE;

    /** Durable last-known snapshot; the live position lives in Redis Geo. */
    @Column(name = "last_location", columnDefinition = "geometry(Point,4326)")
    private Point lastLocation;

    @Column(name = "last_location_at")
    private Instant lastLocationAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Point getLastLocation() { return lastLocation; }
    public void setLastLocation(Point lastLocation) { this.lastLocation = lastLocation; }

    public Instant getLastLocationAt() { return lastLocationAt; }
    public void setLastLocationAt(Instant lastLocationAt) { this.lastLocationAt = lastLocationAt; }
}
