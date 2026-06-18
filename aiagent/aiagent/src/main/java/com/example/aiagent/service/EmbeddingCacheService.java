package com.example.aiagent.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.AgentAnalysis;

@Service
public class EmbeddingCacheService {

	private final Map<String, List<Double>> cache = new ConcurrentHashMap<>();

	private final Map<String, AgentAnalysis> analysisCache = new ConcurrentHashMap<>();

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

	public AgentAnalysis getAnalysis(String text) {

		String key = text.toLowerCase().trim();

		AgentAnalysis analysis = analysisCache.get(key);

		if (analysis != null) {
			System.out.println("Analysis Cache HIT = " + key);
		}
		else {
			System.out.println("Analysis Cache MISS = " + key);
		}

		return analysis;
	}

	public void saveAnalysis(String text, AgentAnalysis analysis) {

		String key = text.toLowerCase().trim();

		analysisCache.put(key, analysis);
	}
}