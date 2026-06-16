package com.example.aiagent.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.repository.MenuItemRepository;
import com.example.aiagent.repository.RestaurantRepository;

@Service
public class EmbeddingMigrationService{
	@Autowired
	private MenuItemRepository menuItemRepository;
	
	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private EmbeddingService embeddingService;
	
	public void generateEmbeddings() {

	    List<MenuItem> items =menuItemRepository.findAll();

	    for(MenuItem item : items) {

	        List<Double> embedding =embeddingService.generateEmbedding(item.getDescription());
	        item.setEmbedding(embedding.toString());
	        menuItemRepository.save(item);

	        System.out.println("Generated embedding for "+ item.getName());
	    }
	}
	
	public void generateRestaurantEmbeddings() {

	    List<Restaurant> restaurants =restaurantRepository.findAll();

	    for(Restaurant restaurant : restaurants) {

	        List<Double> embedding =embeddingService.generateEmbedding(restaurant.getDescription());

	        restaurant.setEmbedding(embedding.toString());

	        restaurantRepository.save(restaurant);

	        System.out.println("Generated embedding for "+ restaurant.getName());
	    }
	}
}
