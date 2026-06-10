package com.example.aiagent.service;

import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Intent;

@Service
public class IntentService {

    public Intent detectIntent(String message) {

        String text = message.toLowerCase();

        if(text.contains("restaurant")|| text.contains("food")) {
            return Intent.RESTAURANT_SEARCH;
        }

        return Intent.GENERAL_CHAT;
    }
}
