package com.example.aiagent.tool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.ToolResult;
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
	public ToolResult execute(String query) {
	    return execute(query, new QueryContext());
	}

	@Override
	public ToolResult execute(String query, QueryContext context) {

		System.out.println("Restaurant Context = " + context);

		List<Restaurant> restaurants;

		boolean hasFilters = context.getRestaurantType() != null || context.getMaxPrice() != null
				|| context.getFoodItem() != null;

		if (!hasFilters) {

			System.out.println("Using Semantic Search");

			restaurants = restaurantService.semanticSearch(query);

		} else {

			System.out.println("Using Structured Search");

			restaurants = restaurantService.searchRestaurants(context);
		}

		if (restaurants.isEmpty()) {

			return new ToolResult("Restaurant Tool", """
					No matching restaurants found.
					""");
		}

		StringBuilder sb = new StringBuilder();

		for (Restaurant restaurant : restaurants) {

			sb.append("Name: ").append(restaurant.getName()).append(", Type: ").append(restaurant.getType())
					.append(", Price: ").append(restaurant.getPrice()).append(", Description: ")
					.append(restaurant.getDescription()).append("\n");
		}

		return new ToolResult("Restaurant Tool", sb.toString());
	}
}