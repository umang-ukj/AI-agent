package com.example.aiagent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aiagent.DTO.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByType(String type);

    List<Restaurant> findByTypeAndPriceLessThanEqual(String type,Integer price);
    
}
