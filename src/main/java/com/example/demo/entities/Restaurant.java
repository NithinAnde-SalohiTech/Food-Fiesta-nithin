package com.example.demo.entities;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @Column(name = "avg_prep_minutes")
    private int avgPrepMinutes = 20;

    @Column(name = "is_active")
    private boolean active = true;

    /** POINT(longitude latitude), SRID 4326. */
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getAvgPrepMinutes() { return avgPrepMinutes; }
    public void setAvgPrepMinutes(int avgPrepMinutes) { this.avgPrepMinutes = avgPrepMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }
}
