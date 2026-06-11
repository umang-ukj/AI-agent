package com.example.aiagent.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


// to summarize messages
@Service
public class SummarizationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public String summarize(List<String> messages) {

        String conversation =String.join("\n", messages);

        String prompt =
        	    """
        	    Summarize the following conversation.

        	    Rules:
        	    - Maximum 2 lines
        	    - No introduction
        	    - No phrases like "Here is a summary"
        	    - Return only the summary

        	    Conversation:
        	    """
        	    + conversation;

        Map<String, Object> requestBody =Map.of("model", ollamaModel,"prompt", prompt,"stream", false);

        Map response =restTemplate.postForObject(ollamaApiUrl,requestBody,Map.class);

        return (String)response.get("response");
    }
}