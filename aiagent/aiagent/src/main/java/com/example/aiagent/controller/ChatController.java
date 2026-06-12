package com.example.aiagent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.service.ChatService;
import com.example.aiagent.service.RestaurantService;


@RestController
@RequestMapping("/api/generate")
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ChatResponse response(@RequestBody ChatRequest chatRequest) {

        return chatService.getResponse(chatRequest);
    }
    
    @GetMapping("/restaurants")
    public List<Restaurant> getRestaurants() {
        return restaurantService.getVegRestaurants();
    }
    
    @GetMapping("/semantic")
    public Object semanticSearch(@RequestParam String query) {

        return restaurantService.semanticSearch(query);
    }
}
