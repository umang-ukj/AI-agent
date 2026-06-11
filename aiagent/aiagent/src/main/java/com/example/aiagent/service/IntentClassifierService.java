package com.example.aiagent.service;

import org.springframework.stereotype.Service;
import com.example.aiagent.DTO.Intent;

@Service
public class IntentClassifierService {

	public Intent classify(String userQuery) {

	    String query = userQuery.toLowerCase();

	    if (query.contains("restaurant")
	            || query.contains("food")
	            || query.contains("veg")
	            || query.contains("vegetarian")
	            || query.contains("non-veg")
	            || query.contains("dinner")
	            || query.contains("lunch")
	            || query.contains("breakfast")
	            || query.contains("meal")) {

	        return Intent.RESTAURANT_SEARCH;
	    }

	    return Intent.GENERAL_CHAT;
	}
}
