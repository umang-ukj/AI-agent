package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.RestaurantScore;
import com.example.aiagent.DTO.RestaurantSearchRequest;
import com.example.aiagent.repository.RestaurantRepository;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository repository;
    
    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private SimilarityService similarityService;

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
    
    public List<Restaurant> searchByDescription(String keyword) {

        return repository.findByDescriptionContainingIgnoreCase(keyword);
    }
    
    public List<Restaurant> semanticSearch(String query) {

        List<Double> queryEmbedding =embeddingService.generateEmbedding(query);

        List<RestaurantScore> scores =new ArrayList<>();

        List<Restaurant> restaurants =repository.findAll();

        for(Restaurant restaurant : restaurants) {

            if(restaurant.getEmbedding() == null|| restaurant.getEmbedding().isBlank()) {
                continue;
            }

            List<Double> descriptionEmbedding =embeddingService.parseEmbedding(restaurant.getEmbedding());

            double similarity =similarityService.cosineSimilarity(queryEmbedding,descriptionEmbedding);

            scores.add(new RestaurantScore(restaurant,similarity));
        }

        scores.sort((a,b) ->Double.compare(b.getScore(),a.getScore()));

        return scores.stream().limit(3).map(RestaurantScore::getRestaurant).toList();
    }
}