package com.example.aiagent.tool;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.service.RestaurantService;

@Component
public class RestaurantTool implements Tool {

    @Autowired
    private RestaurantService restaurantService;
    
    @Override
    public Intent supportedIntent() {
        return Intent.RESTAURANT_SEARCH;
    }
    
    @Override
    public String execute(String query) {

        Pattern pattern =Pattern.compile("under\\s+(\\d+)");

        Matcher matcher =pattern.matcher(query.toLowerCase());

        Integer budget = null;

        if (matcher.find()) {
            budget =Integer.parseInt(matcher.group(1));
        }

        List<Restaurant> restaurants;

        if (budget != null) {
            restaurants =restaurantService.getVegRestaurantsUnderBudget(budget);
        } 
        else {
            restaurants =restaurantService.getVegRestaurants();
        }

        return """
                Use ONLY the restaurants provided below.

                Restaurant Data:
                """
                + restaurants
                + "\n\n";
    }
}