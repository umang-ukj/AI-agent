package com.example.aiagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;

@Service
public class ChatService {
	
	public ChatResponse getResponse(ChatRequest chatRequest) {

        ChatResponse response = new ChatResponse();

        response.setResponse(
            "AI received message: " + chatRequest.getMessage()
        );

        return response;
    }

}
