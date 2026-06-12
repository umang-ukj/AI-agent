package com.example.aiagent.service;

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

        String url =ollamaBaseUrl+ "/api/embeddings";

        Map<String, Object> request =Map.of("model","nomic-embed-text","prompt",text);

        Map response =restTemplate.postForObject(url,request,Map.class);

        return (List<Double>)response.get("embedding");
    }
}