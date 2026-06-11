package com.example.aiagent.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.RestaurantSearchRequest;

@Service
public class ParameterExtractionService {

    public RestaurantSearchRequest extract(String query) {

        RestaurantSearchRequest request =new RestaurantSearchRequest();

        String lower =query.toLowerCase();

        if(lower.contains("non veg")|| lower.contains("non-veg")|| lower.contains("non vegetarian")
        		|| lower.contains("non-vegetarian")) {

            request.setType("Non-Vegetarian");
        }
        else if(lower.contains("veg")|| lower.contains("vegetarian")) {

            request.setType("Vegetarian");
        }

        Pattern pattern =Pattern.compile("under\\s+(\\d+)");

        Matcher matcher =pattern.matcher(lower);

        if(matcher.find()) {
        	
            request.setBudget(Integer.parseInt(matcher.group(1)));
        }

        return request;
    }
}
