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
				MENU_SEARCH
				MENU_LOOKUP
				PRICE_LOOKUP
				RESTAURANT_MENU_QUERY
				GENERAL_CHAT

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
