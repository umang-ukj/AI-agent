package com.example.aiagent.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;
import com.example.aiagent.DTO.Intent;
import com.example.aiagent.tool.Tool;
@Service
public class ChatService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private IntentService intentService;
    
    @Autowired
    private IntentClassifierService intentClassifierService;
    
    @Autowired
    private MemoryService memoryService;
    
    @Autowired
    private ToolRegistry toolRegistry;
    
    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public ChatResponse getResponse(ChatRequest chatRequest) {

        String userMessage = chatRequest.getMessage();
        
        Intent intent =intentClassifierService.classify(userMessage);
        
        String sessionId = chatRequest.getSessionId();
        
        //adding msg to in-memoryDB based on session ID
        memoryService.addMessage(sessionId,"user", userMessage);
        
        //convo history retrival based on sessionID
        List<String> history =memoryService.getConversation(sessionId);

        Tool tool =toolRegistry.getTool(intent);

        String toolContext = "";

        if(tool != null) {
            toolContext =tool.execute(userMessage);
        }
        
     // Final prompt
        String fullPrompt =toolContext +String.join("\n", history);
        
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