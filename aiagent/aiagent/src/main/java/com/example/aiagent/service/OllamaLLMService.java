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
    	long start = System.currentTimeMillis();

    	Map<String, Object> requestBody = Map.of("model", ollamaModel,"prompt", prompt,"stream", false,"options",
    			Map.of("num_predict", 60));
        System.out.println("Model = " + ollamaModel);
        System.out.println("Prompt Length = " + prompt.length());

        Map response =restTemplate.postForObject(ollamaApiUrl,requestBody,Map.class);
        
        System.out.println("LLM Call Time = "+ (System.currentTimeMillis() - start) + " ms");

        String result = (String) response.get("response");

        System.out.println("Response Length = " + result.length());

        return result;
    }
}