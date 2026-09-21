package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entities.Driver;
import com.example.demo.entities.Driver.Status;

public interface DriverRepository extends CrudRepository<Driver, Long> {

    List<Driver> findByStatus(Status status);
}
