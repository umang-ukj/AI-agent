package com.example.aiagent.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.RestaurantSearchRequest;
import com.example.aiagent.repository.RestaurantRepository;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository repository;

    public List<Restaurant> getVegRestaurants() {

        return repository.findByType("Vegetarian");
    }
    
    public List<Restaurant> getVegRestaurantsUnderBudget(Integer budget) {
        return repository.findByTypeAndPriceLessThanEqual("Vegetarian",budget);
    }
    public List<Restaurant> searchRestaurants(RestaurantSearchRequest request) {

        if(request.getBudget() != null && request.getType() != null) {

            return repository.findByTypeAndPriceLessThanEqual(request.getType(),request.getBudget());
        }

        if(request.getType() != null) {

            return repository.findByType(request.getType());
        }

        return repository.findAll();
    }
}