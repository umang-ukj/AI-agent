package com.example.aiagent.service;

import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Intent;

@Service
public class IntentClassifierService {

public Intent classify(String message) {

    String lower = message.toLowerCase();

    // PRICE LOOKUP

    if (containsAny(lower,
            "price",
            "cost",
            "cheap",
            "cheapest",
            "expensive")) {

        return Intent.PRICE_LOOKUP;
    }

    // RESTAURANT -> MENU RELATION

    if (lower.contains("restaurant")
            && (lower.contains("serve")
            || lower.contains("serves"))) {

        return Intent.RESTAURANT_MENU_QUERY;
    }

    // MENU LOOKUP

    if (containsAny(lower,
            "dish",
            "dishes",
            "food",
            "foods",
            "meal",
            "meals",
            "menu",
            "item",
            "items")) {

        return Intent.MENU_LOOKUP;
    }

    // MENU SEARCH

    if (containsAny(lower,
            "paneer",
            "chicken",
            "protein",
            "salad",
            "wrap",
            "shake")) {

        return Intent.MENU_SEARCH;
    }

    // RESTAURANT SEARCH

    if (containsAny(lower,
            "vegetarian",
            "non vegetarian",
            "restaurant")) {

        return Intent.RESTAURANT_SEARCH;
    }

    return Intent.GENERAL_CHAT;
}

public boolean isFollowUp(String query) {

    query = query.toLowerCase();

    return query.contains("only")
            || query.contains("cheapest")
            || query.contains("costliest")
            || query.contains("expensive")
            || query.contains("price")
            || query.contains("cost")
            || query.contains("that")
            || query.contains("those")
            || query.contains("them")
            || query.contains("one")
            || query.contains("it")
            || query.contains("other")
            || query.contains("more")
            || query.contains("else");
}

private boolean containsAny(String message, String... words) {

    for (String word : words) {
        if (message.contains(word)) {
            return true;
        }
    }

    return false;
}

}
