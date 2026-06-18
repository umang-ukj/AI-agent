package com.example.aiagent.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.QueryContext;

@Service
public class QueryUnderstandingService {

	@Autowired
	private OpenAiLLMService openAi;

	public QueryContext extract(String userQuery) {

		String prompt = """
				Extract restaurant search filters.

				Return ONLY valid JSON.

				Example:

				{
				  "foodItem":"paneer",
				  "restaurantType":"vegetarian",
				  "maxPrice":300
				}

				Use null when not present.

				User Query:
				""" + userQuery;

		String response = openAi.generate(prompt, 0);

		System.out.println("QUERY EXTRACTION RESPONSE = " + response);

		JSONObject json = new JSONObject(response);

		QueryContext context = new QueryContext();

		if (!json.isNull("foodItem")) {
			context.setFoodItem(json.getString("foodItem"));
		}

		if (!json.isNull("restaurantType")) {
			context.setRestaurantType(json.getString("restaurantType"));
		}

		if (!json.isNull("maxPrice")) {
			context.setMaxPrice(json.getInt("maxPrice"));
		}

		return context;
	}
}