package com.example.aiagent.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingCacheService {

	private final Map<String, List<Double>> cache = new ConcurrentHashMap<>();

	@Autowired
	private EmbeddingService embeddingService;

	public List<Double> getEmbedding(String text) {

		String key = text.toLowerCase().trim();

		if (cache.containsKey(key)) {

			System.out.println("Embedding Cache HIT = " + key);

			return cache.get(key);
		}

		System.out.println("Embedding Cache MISS = " + key);

		List<Double> embedding = embeddingService.generateEmbedding(text);

		cache.put(key, embedding);

		return embedding;
	}
}