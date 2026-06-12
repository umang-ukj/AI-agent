package com.example.aiagent.tool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.RestaurantSearchRequest;
import com.example.aiagent.service.ParameterExtractionService;
import com.example.aiagent.service.RestaurantService;

@Component
public class RestaurantTool implements Tool {

    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private ParameterExtractionService parameterExtractionService;
    @Override
    public Intent supportedIntent() {
        return Intent.RESTAURANT_SEARCH;
    }
    
    @Override
    public String execute(String query) {
        
    	RestaurantSearchRequest request =parameterExtractionService.extract(query);

    	List<Restaurant> restaurants;

    	if(request.getType() == null&& request.getBudget() == null) {

    	    System.out.println("Using Semantic Search");

    	    restaurants =restaurantService.semanticSearch(query);

    	} else {

    	    System.out.println("Using Structured Search");

    	    restaurants =restaurantService.searchRestaurants(request);
    	}
        
        if(restaurants.isEmpty()) {
            return """
                   No matching restaurants found.
                   """;
        }

        return """
                Use ONLY the restaurants provided below.

                Restaurant Data:
                """
                + restaurants
                + "\n\n";
    }
}