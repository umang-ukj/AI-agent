package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ollama.base.url}")
    private String ollamaBaseUrl;

    public List<Double> generateEmbedding(String text) {
    	long start = System.currentTimeMillis();


        String url =ollamaBaseUrl+ "/api/embeddings";

        Map<String, Object> request =Map.of("model","nomic-embed-text","prompt",text);

        Map response =restTemplate.postForObject(url,request,Map.class);
        
        System.out.println("Embedding Call Time = "+ (System.currentTimeMillis() - start) + " ms");

        return (List<Double>)response.get("embedding");
    }
    
    public String toString(List<Double> embedding) {

        return embedding.toString();
    }
    public List<Double> fromString(String embeddingText) {

        String cleaned =embeddingText.replace("[", "").replace("]", "");

        String[] values =cleaned.split(",");

        List<Double> embedding =new ArrayList<>();

        for(String value : values) {

            embedding.add(Double.parseDouble(value.trim()));
        }

        return embedding;
    }
    
    public List<Double> parseEmbedding(String embeddingString) {

        if (embeddingString == null || embeddingString.isBlank()) {
            return List.of();
        }

        embeddingString = embeddingString.replace("[", "").replace("]", "");

        String[] values = embeddingString.split(",");

        List<Double> embedding = new ArrayList<>();

        for (String value : values) {
            embedding.add(Double.parseDouble(value.trim()));
        }

        return embedding;
    }
}