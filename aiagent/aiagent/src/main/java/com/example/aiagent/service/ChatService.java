package com.example.aiagent.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;
import com.example.aiagent.DTO.Restaurant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class ChatService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private MemoryService memoryService;
    
    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public ChatResponse getResponse(ChatRequest chatRequest) {

        String userMessage = chatRequest.getMessage();
        
        String sessionId = chatRequest.getSessionId();
        
        //adding msg to in-memoryDB based on session ID
        memoryService.addMessage(sessionId,"user", userMessage);
        
        //convo history retrival based on sessionID
        List<String> history =
                memoryService.getConversation(sessionId);
        
     // Basic RAG
        String restaurantContext = "";

        if (userMessage.toLowerCase().contains("restaurant")|| userMessage.toLowerCase().contains("food")) {

            Pattern pattern =Pattern.compile("under\\s+(\\d+)");

            Matcher matcher =pattern.matcher(userMessage.toLowerCase());

            Integer budget = null;

            if (matcher.find()) {
                budget = Integer.parseInt(matcher.group(1));
            }

            List<Restaurant> restaurants;

            if (budget != null) {
                restaurants =restaurantService.getVegRestaurantsUnderBudget(budget);
            } 
            else {
                restaurants =restaurantService.getVegRestaurants();
            }

            restaurantContext =
                    """
                    Use ONLY the restaurants provided below.

                    Restaurant Data:
                    """
                    + restaurants
                    + "\n\n";
        }

     // Final prompt
        String fullPrompt =
                restaurantContext +
                String.join("\n", history);
        
        // Ollama request body
        Map<String, Object> requestBody = Map.of(
                "model", ollamaModel,
                "prompt", fullPrompt,
                "stream", false
        );

        // Call Ollama API
        Map response = restTemplate.postForObject(
                ollamaApiUrl,
                requestBody,
                Map.class
        );

        // Extract response text
        String aiText = (String) response.get("response");
        memoryService.addMessage(sessionId,"AI", aiText);

        // Return DTO
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setResponse(aiText);

        return chatResponse;
    }
}