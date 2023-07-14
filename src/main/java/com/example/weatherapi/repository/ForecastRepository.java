package com.example.weatherapi.repository;

import com.example.weatherapi.model.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    Optional<Forecast> findByCityAndDate(String city, LocalDate date);
    List<Forecast> findAllByOrderByCityAsc();

}

