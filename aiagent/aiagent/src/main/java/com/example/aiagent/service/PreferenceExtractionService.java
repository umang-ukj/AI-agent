package com.example.aiagent.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.UserPreferences;

@Service
public class PreferenceExtractionService {

	public UserPreferences extractPreferences(String message) {

		UserPreferences preferences = new UserPreferences();

		String lower = message.toLowerCase();

		if (lower.contains("vegetarian")) {

			preferences.setDietType("Vegetarian");
		}

		if (lower.contains("high protein")) {

			preferences.setFitnessGoal("High Protein");
		}

		Pattern pattern = Pattern.compile("under\\s+(\\d+)");

		Matcher matcher = pattern.matcher(lower);

		if (matcher.find()) {

			preferences.setBudget(Integer.parseInt(matcher.group(1)));
		}

		return preferences;
	}

	public boolean isPreferenceStatement(String message) {

		String lower = message.toLowerCase();

		return lower.contains("i am") || lower.contains("i'm") || lower.contains("my budget")
				|| lower.contains("i prefer") || lower.contains("remember") || lower.contains("i like");
	}
}