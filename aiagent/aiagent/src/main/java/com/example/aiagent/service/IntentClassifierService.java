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
                Classify the query into one of:

                RESTAURANT_SEARCH
                GENERAL_CHAT

                Return ONLY the intent name.

                Query:
                """ + userQuery;

        Map<String, Object> requestBody = Map.of("model", ollamaModel,"prompt", prompt,"stream", false);

        Map response = restTemplate.postForObject(ollamaApiUrl,requestBody,Map.class);

        String intentResponse =((String) response.get("response")).trim();

        try {
            return Intent.valueOf(intentResponse);
        } catch (Exception e) {
            return Intent.GENERAL_CHAT;
        }
    }
}
