package com.example.aiagent.service;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OllamaLLMService implements LLMService {

    private final RestTemplate restTemplate;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public OllamaLLMService(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public String generate(String prompt) {

        Map<String, Object> requestBody =Map.of("model", ollamaModel,"prompt", prompt,"stream", false);

        Map response =restTemplate.postForObject(ollamaApiUrl,requestBody,Map.class);

        return (String) response.get("response");
    }
}