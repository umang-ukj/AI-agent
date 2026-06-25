package com.example.aiagent.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.Restaurant;

@Component
public class PromptFormatter {

	@SuppressWarnings("unchecked")
	public String format(Object data) {

		if (data == null) {
			return "";
		}

		if (data instanceof MenuItem item) {
			return formatMenuItem(item);
		}

		if (data instanceof Restaurant restaurant) {
			return formatRestaurant(restaurant);
		}

		if (data instanceof List<?> list) {
			if (list.isEmpty()) {
				return "";
			}

			Object first = list.get(0);

			if (first instanceof MenuItem) {
				return formatMenuItems((List<MenuItem>) list);
			}

			if (first instanceof Restaurant) {
				return formatRestaurants((List<Restaurant>) list);
			}
		}

		return data.toString();
	}

	private String formatMenuItem(MenuItem item) {

		return String.format("""
				%s

				Protein : %dg
				Calories : %d
				Carbs : %dg
				Fat : %dg
				Price : Rs. %d
				""", item.getName(), item.getProtein(), item.getCalories(), item.getCarbs(), item.getFat(),
				item.getPrice());
	}

	private String formatMenuItems(List<MenuItem> items) {

		StringBuilder sb = new StringBuilder();

		for (MenuItem item : items) {

			if (item.getRestaurant() != null) {
				sb.append("Restaurant: ").append(item.getRestaurant().getName()).append("\n");
			}

			sb.append("Name: ").append(item.getName()).append("\n");

			sb.append("Price: Rs. ").append(item.getPrice()).append("\n");

			if (item.getDescription() != null) {
				sb.append("Description: ").append(item.getDescription()).append("\n");
			}

			sb.append("Protein: ").append(item.getProtein()).append("g\n");

			sb.append("Calories: ").append(item.getCalories()).append("\n");

			sb.append("Carbs: ").append(item.getCarbs()).append("g\n");

			sb.append("Fat: ").append(item.getFat()).append("g\n");

			sb.append("\n");
		}

		return sb.toString();
	}

	private String formatRestaurant(Restaurant restaurant) {

		return """
				Restaurant: %s
				Type: %s
				Price: Rs. %d
				Description: %s
				""".formatted(restaurant.getName(), restaurant.getType(), restaurant.getPrice(),
				restaurant.getDescription());
	}

	private String formatRestaurants(List<Restaurant> restaurants) {

		StringBuilder sb = new StringBuilder();

		for (Restaurant restaurant : restaurants) {

			sb.append(formatRestaurant(restaurant)).append("\n");
		}

		return sb.toString();
	}
}