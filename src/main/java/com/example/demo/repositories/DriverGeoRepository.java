package com.example.demo.repositories;

import com.example.demo.config.RedisConfig;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Thin wrapper over Redis GEO commands (GEOADD / GEORADIUS / GEOPOS) that holds
 * the hot, high-frequency live position of every online driver.
 */
@Repository
public class DriverGeoRepository {

    private final StringRedisTemplate redis;

    public DriverGeoRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** High-throughput write: upsert a driver's live position (GEOADD). */
    public void updateLocation(long driverId, double lon, double lat) {
        redis.opsForGeo().add(RedisConfig.DRIVER_GEO_KEY,
                new Point(lon, lat), String.valueOf(driverId));
    }

    public void removeDriver(long driverId) {
        redis.opsForZSet().remove(RedisConfig.DRIVER_GEO_KEY, String.valueOf(driverId));
    }

    public Optional<Point> position(long driverId) {
        List<Point> pts = redis.opsForGeo()
                .position(RedisConfig.DRIVER_GEO_KEY, String.valueOf(driverId));
        return (pts == null || pts.isEmpty() || pts.get(0) == null)
                ? Optional.empty() : Optional.of(pts.get(0));
    }

    /** Kafka routing consumer uses this: nearest drivers within radius (meters). */
    public List<GeoResult<GeoLocation<String>>> findNearby(double lon, double lat, double radiusMeters) {
        Circle within = new Circle(new Point(lon, lat),
                new Distance(radiusMeters, DistanceUnit.METERS));
        GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates()
                .includeDistance()
                .sortAscending()
                .limit(10);
        var results = redis.opsForGeo().radius(RedisConfig.DRIVER_GEO_KEY, within, args);
        return results == null ? List.of() : results.getContent();
    }
}
