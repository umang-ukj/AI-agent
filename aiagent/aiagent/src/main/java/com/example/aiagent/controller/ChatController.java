package com.example.aiagent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;
import com.example.aiagent.service.ChatService;


@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatResponse response(@RequestBody ChatRequest chatRequest) {

        return chatService.getResponse(chatRequest);
    }
}
