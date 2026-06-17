package com.example.aiagent.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.repository.RestaurantRepository;

import jakarta.annotation.PostConstruct;

@Service
public class RestaurantEmbeddingCacheService {

	private final Map<Long, List<Double>> cache = new ConcurrentHashMap<>();

	@Autowired
	private RestaurantRepository repository;

	@Autowired
	private EmbeddingService embeddingService;

	@PostConstruct
	public void loadCache() {

		for (Restaurant restaurant : repository.findAll()) {

			if (restaurant.getEmbedding() == null || restaurant.getEmbedding().isBlank()) {

				System.out.println("Skipping Restaurant: " + restaurant.getName());

				continue;
			}

			cache.put(restaurant.getId(), embeddingService.parseEmbedding(restaurant.getEmbedding()));
		}

		System.out.println("Loaded Restaurant Embeddings = " + cache.size());
	}

	public List<Double> getEmbedding(Long restaurantId) {

		return cache.get(restaurantId);
	}
}
