package com.example.aiagent.service;

import org.springframework.stereotype.Service;
import com.example.aiagent.DTO.Intent;

@Service
public class IntentClassifierService {

	public Intent classify(String userQuery) {

	    String query = userQuery.toLowerCase();

	    if(query.contains("gym")|| query.contains("protein")|| query.contains("diet")
	            || query.contains("healthy")|| query.contains("meal")|| query.contains("food")
	            || query.contains("menu")|| query.contains("price")|| query.contains("cost")
	            || query.contains("item")|| query.contains("dish"))
	    {
	        return Intent.MENU_SEARCH;
	    }
	    if(query.contains("restaurant")|| query.contains("veg")|| query.contains("vegetarian")|| query.contains("non-veg")
	            || query.contains("dinner")|| query.contains("lunch")|| query.contains("breakfast")) {

	        return Intent.RESTAURANT_SEARCH;
	    }

	    return Intent.GENERAL_CHAT;
	}
	
	public boolean isFollowUp(String userQuery) {

	    String query = userQuery.toLowerCase();

	    return query.contains("only")|| query.contains("cheapest")|| query.contains("costliest")
	            || query.contains("expensive")|| query.contains("price")|| query.contains("cost")
	            || query.contains("that")|| query.contains("those")|| query.contains("them")|| query.contains("one");
	}
}
