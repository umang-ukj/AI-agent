package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.MenuItemScore;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.repository.MenuItemRepository;

@Service
public class MenuItemService {

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private MenuEmbeddingCacheService menuEmbeddingCacheService;

	@Autowired
	private EmbeddingCacheService embeddingCacheService;

	@Autowired
	private SimilarityService similarityService;

	public List<MenuItem> semanticSearch(String query) {

		return semanticSearch(query, null);
	}

	public List<MenuItem> semanticSearch(String query, Restaurant restaurant) {

		String lowerQuery = query.toLowerCase();

		List<MenuItem> menuItems;

		if (restaurant != null) {

			System.out.println("Filtering menu by restaurant = " + restaurant.getName());

			menuItems = menuItemRepository.findByRestaurant(restaurant);

		} else {

			menuItems = menuItemRepository.findAll();
		}

		if (lowerQuery.contains("non veg") || lowerQuery.contains("non-veg") || lowerQuery.contains("chicken")
				|| lowerQuery.contains("meat")) {

			menuItems = menuItems.stream().filter(item -> item.getDescription().toLowerCase().contains("chicken")
					|| item.getDescription().toLowerCase().contains("meat")).toList();
		}

		if (lowerQuery.contains("veg") || lowerQuery.contains("vegetarian")) {

			menuItems = menuItems.stream().filter(item -> item.getDescription().toLowerCase().contains("vegetarian"))
					.toList();
		}

		List<Double> queryEmbedding = embeddingCacheService.getEmbedding(query);

		List<MenuItemScore> scores = new ArrayList<>();

		for (MenuItem item : menuItems) {

			List<Double> itemEmbedding = menuEmbeddingCacheService.getEmbedding(item.getId());

			double similarity = similarityService.cosineSimilarity(queryEmbedding, itemEmbedding);

			System.out.println(item.getName() + " -> " + similarity);

			scores.add(new MenuItemScore(item, similarity));
		}

		scores.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

		double bestScore = scores.get(0).getScore();

		double threshold = bestScore * 0.95;

		System.out.println("Threshold = " + threshold);

		scores.forEach(score -> System.out.println(score.getMenuItem().getName() + " -> " + score.getScore()));

		return scores.stream().filter(score -> score.getScore() >= threshold).limit(5).map(MenuItemScore::getMenuItem)
				.toList();
	}

	public List<MenuItem> structuredSearch(QueryContext context, Restaurant restaurant) {

		List<MenuItem> menuItems;

		if (restaurant != null) {

			menuItems = menuItemRepository.findByRestaurant(restaurant);

		} else {

			menuItems = menuItemRepository.findAll();
		}

		System.out.println("Applying Food Filter = " + context.getFoodItem());

		System.out.println("Applying Restaurant Type Filter = " + context.getRestaurantType());

		System.out.println("Applying Max Price Filter = " + context.getMaxPrice());

		if (context.getFoodItem() != null) {

			String food = context.getFoodItem().toLowerCase();

			menuItems = menuItems.stream()
					.filter(item -> item.getRestaurant().getType().equalsIgnoreCase(context.getRestaurantType()))
					.toList();
		}

		if (context.getRestaurantType() != null) {

			String restaurantType = context.getRestaurantType();

			menuItems = menuItems.stream()
					.filter(item -> item.getRestaurant().getType().equalsIgnoreCase(restaurantType)).toList();
		}

		if (context.getMaxPrice() != null) {

			Integer maxPrice = context.getMaxPrice();

			menuItems = menuItems.stream().filter(item -> item.getPrice() <= maxPrice).toList();
		}

		System.out.println("Structured Search Results = " + menuItems.size());

		return menuItems;
	}
}
