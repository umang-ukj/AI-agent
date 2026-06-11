package com.example.aiagent.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.aiagent.DTO.Intent;

@Service
public class IntentClassifierService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public Intent classify(String userQuery) {

    	String prompt = """
    			You are an intent classifier.

    			Possible intents:

    			1. RESTAURANT_SEARCH
    			Use this ONLY if the user is asking about:
    			- restaurants
    			- food
    			- meals
    			- dining
    			- vegetarian food
    			- non-vegetarian food
    			- restaurant recommendations

    			2. GENERAL_CHAT
    			Use this for everything else.

    			Return ONLY one of:
    			RESTAURANT_SEARCH
    			GENERAL_CHAT

    			Query:
    			"""
    			+ userQuery;

        Map<String, Object> requestBody = Map.of("model", ollamaModel,"prompt", prompt,"stream", false);

        Map response = restTemplate.postForObject(ollamaApiUrl,requestBody,Map.class);

        String intentResponse =((String) response.get("response")).trim();
        
        System.out.println("Intent Response = "+ intentResponse);

        try {
            return Intent.valueOf(intentResponse);
        } catch (Exception e) {
            return Intent.GENERAL_CHAT;
        }
    }
}
