package com.example.aiagent.tool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.ToolResult;
import com.example.aiagent.repository.MenuItemRepository;
import com.example.aiagent.repository.RestaurantRepository;

@Component
public class RestaurantMenuTool implements Tool {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Override
	public ToolResult execute(String query, QueryContext context) {

		if (context == null || context.getRestaurantName() == null) {
			return new ToolResult("RestaurantMenu Tool", "Restaurant name not found.");
		}

		Restaurant restaurant = restaurantRepository.findAll().stream()
				.filter(r -> r.getName().equalsIgnoreCase(context.getRestaurantName())).findFirst().orElse(null);

		if (restaurant == null) {
			return new ToolResult("RestaurantMenu Tool", "Restaurant not found.");
		}

		List<MenuItem> items = menuItemRepository.findByRestaurant(restaurant);

		if (items.isEmpty()) {
			return new ToolResult("RestaurantMenu Tool", "No menu items found.");
		}

		return new ToolResult("RestaurantMenu Tool", "Found " + items.size() + " menu items", items);
	}

	@Override
	public Intent supportedIntent() {
		return Intent.RESTAURANT_MENU_QUERY;
	}

	@Override
	public ToolResult execute(String query) {
		return new ToolResult("RestaurantMenu Tool", "Restaurant menu query requires context");

	}
}