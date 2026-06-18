package com.example.aiagent.tool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.repository.MenuItemRepository;
import com.example.aiagent.repository.RestaurantRepository;
import com.example.aiagent.service.MenuItemService;
import com.example.aiagent.service.QueryUnderstandingService;

@Component
public class RestaurantMenuTool implements Tool {

	@Autowired
	private MenuItemService menuItemService;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private QueryUnderstandingService queryUnderstandingService;

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Override
	public String execute(String query) {

		String lower = query.toLowerCase();

		// Menu listing requests
		if (lower.contains("other") || lower.contains("more") || lower.contains("menu") || lower.contains("dishes")) {

			List<Restaurant> restaurants = restaurantRepository.findAll();

			Restaurant matchedRestaurant = null;

			for (Restaurant restaurant : restaurants) {

				if (lower.contains(restaurant.getName().toLowerCase())) {

					matchedRestaurant = restaurant;
					break;
				}
			}

			if (matchedRestaurant != null) {

				List<MenuItem> items = menuItemRepository.findByRestaurant(matchedRestaurant);

				StringBuilder result = new StringBuilder();

				result.append("Restaurant: ").append(matchedRestaurant.getName()).append("\n\n");

				result.append("Menu Items:\n");

				for (MenuItem item : items) {

					result.append(item.getName()).append(" - ").append(item.getPrice()).append("\n");
				}

				return result.toString();
			}
		}

		QueryContext context = queryUnderstandingService.extract(query);

		List<MenuItem> items = menuItemService.structuredSearch(context, null);

		if (items.isEmpty()) {

			System.out.println("Structured Search Failed. Falling back to Semantic Search");

			items = menuItemService.semanticSearch(query);
		}

		if (items.isEmpty()) {
			return "No matching restaurant found.";
		}

		StringBuilder result = new StringBuilder();

		for (MenuItem item : items) {

			Restaurant restaurant = item.getRestaurant();

			result.append("Restaurant: ").append(restaurant.getName()).append("\n");

			result.append("Menu Item: ").append(item.getName()).append("\n");

			result.append("Price: ").append(item.getPrice()).append("\n\n");
		}

		return result.toString();
	}

	@Override
	public Intent supportedIntent() {

		return Intent.RESTAURANT_MENU_QUERY;
	}
}