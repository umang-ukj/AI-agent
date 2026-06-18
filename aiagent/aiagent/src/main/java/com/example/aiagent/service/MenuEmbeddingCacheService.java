package com.example.aiagent.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.repository.MenuItemRepository;
import jakarta.annotation.PostConstruct;

@Service
public class MenuEmbeddingCacheService {

	private final Map<Long, List<Double>> cache = new ConcurrentHashMap<>();

	@Autowired
	private MenuItemRepository repository;

	@Autowired
	private EmbeddingService embeddingService;

	@PostConstruct
	public void loadCache() {

		for (MenuItem menu : repository.findAll()) {

			if (menu.getEmbedding() == null || menu.getEmbedding().isBlank()) {

				System.out.println("Skipping Menu Item: " + menu.getName());

				continue;
			}

			cache.put(menu.getId(), embeddingService.parseEmbedding(menu.getEmbedding()));
		}

		System.out.println("Loaded Menu Embeddings = " + cache.size());
	}

	public List<Double> getEmbedding(Long menuId) {

		return cache.get(menuId);
	}
}
