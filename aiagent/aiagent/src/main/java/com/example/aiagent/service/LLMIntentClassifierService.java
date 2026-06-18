package com.example.aiagent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Intent;

@Service
public class LLMIntentClassifierService {

	@Autowired
	private LLMProvider llmProvider;

	public Intent classify(String userMessage) {

		String prompt = """
				Classify the user request.

				Return ONLY ONE of:

				RESTAURANT_SEARCH
				RESTAURANT_MENU_QUERY
				MENU_SEARCH
				GENERAL_CHAT

				Rules:

				RESTAURANT_SEARCH:
				- find restaurants
				- suggest restaurants
				- veg restaurants
				- non veg restaurants
				- restaurants under budget

				RESTAURANT_MENU_QUERY:
				- show menu of a restaurant
				- dishes available in a restaurant
				- what does restaurant serve
				- more dishes from a restaurant

				MENU_SEARCH:
				- paneer dishes
				- chicken dishes
				- high protein meals
				- cheapest food item
				- food item under budget

				GENERAL_CHAT:
				- greetings
				- jokes
				- general questions

				User Request:
				""" + userMessage;

		try {

			String response = llmProvider.getService().generate(prompt).trim().toUpperCase();

			System.out.println("LLM CLASSIFIER RESPONSE = " + response);

			return Intent.valueOf(response);

		} catch (Exception e) {

			return Intent.GENERAL_CHAT;
		}
	}
}
