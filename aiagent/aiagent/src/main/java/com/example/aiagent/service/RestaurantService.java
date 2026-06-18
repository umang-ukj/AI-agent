package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.RestaurantScore;
import com.example.aiagent.repository.RestaurantRepository;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.repository.MenuItemRepository;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository repository;
	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private RestaurantEmbeddingCacheService restaurantEmbeddingCacheService;

	@Autowired
	private EmbeddingCacheService embeddingCacheService;

	@Autowired
	private SimilarityService similarityService;

	public List<Restaurant> getVegRestaurants() {

		return repository.findByType("Vegetarian");
	}

	public List<Restaurant> getVegRestaurantsUnderBudget(Integer budget) {

		return repository.findByTypeAndPriceLessThanEqual("Vegetarian", budget);
	}

	public List<Restaurant> searchRestaurants(QueryContext context) {

		List<Restaurant> restaurants = repository.findAll();

		// Food item filter
		if (context.getFoodItem() != null) {

			List<MenuItem> menuItems = menuItemRepository.findByNameContainingIgnoreCase(context.getFoodItem());

			restaurants = menuItems.stream().map(MenuItem::getRestaurant).distinct().toList();
		}

		// Restaurant type filter
		if (context.getRestaurantType() != null) {

			restaurants = restaurants.stream().filter(r -> r.getType().equalsIgnoreCase(context.getRestaurantType()))
					.toList();
		}

		// Budget filter
		if (context.getMaxPrice() != null) {

			restaurants = restaurants.stream().filter(r -> r.getPrice() <= context.getMaxPrice()).toList();
		}

		return restaurants;
	}

	public List<Restaurant> searchByDescription(String keyword) {

		return repository.findByDescriptionContainingIgnoreCase(keyword);
	}

	public List<Restaurant> semanticSearch(String query) {

		List<Double> queryEmbedding = embeddingCacheService.getEmbedding(query);

		List<RestaurantScore> scores = new ArrayList<>();

		List<Restaurant> restaurants = repository.findAll();

		for (Restaurant restaurant : restaurants) {

			if (restaurant.getEmbedding() == null || restaurant.getEmbedding().isBlank()) {
				continue;
			}

			List<Double> descriptionEmbedding = restaurantEmbeddingCacheService.getEmbedding(restaurant.getId());

			double similarity = similarityService.cosineSimilarity(queryEmbedding, descriptionEmbedding);

			scores.add(new RestaurantScore(restaurant, similarity));
		}

		scores.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

		return scores.stream().limit(3).map(RestaurantScore::getRestaurant).toList();
	}
}