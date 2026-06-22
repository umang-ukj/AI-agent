package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.AgentAnalysis;
import com.example.aiagent.DTO.Intent;

@Service
public class AgentAnalysisService {

	@Autowired
	private LLMProvider llmProvider;

	@Autowired
	private EmbeddingCacheService cacheService;

	public AgentAnalysis analyze(String userMessage) {

		AgentAnalysis cached = cacheService.getAnalysis(userMessage);

		if (cached != null) {
			return cached;
		}

		String prompt = """
																																				Analyze the user request.
																																				If multiple tools are required,
																						                                                        populate the tools array.
																						                                                        Otherwise return a single tool.

																																				Return ONLY valid JSON.

																																				{
																		  "intent":"",
																		  "memoryUpdate":false,
																		  "dietType":null,
																		  "fitnessGoal":null,
																		  "budget":null,
																		  "foodItem":null,
																		  "restaurantName":null,
																		  "nutritionMetric":null,
																		  "sortOrder":null,
																		  "tools":[]
																		}

																																						Intent must be one of:

																																						RESTAURANT_SEARCH
																																						RESTAURANT_MENU_QUERY
																																						MENU_SEARCH
																																						GENERAL_CHAT
																																						RECOMMENDATION
																																						NUTRITION_QUERY

																																						Examples:

																																						User: I am vegetarian

																																						{
																																						  "intent":"GENERAL_CHAT",
																																						  "memoryUpdate":true,
																																						  "dietType":"Vegetarian"
																																						}

																																						User: My budget is 500

																																						{
																																						  "intent":"GENERAL_CHAT",
																																						  "memoryUpdate":true,
																																						  "budget":500
																																						}

																																						User: Suggest food under 500

																																						{
																																						  "intent":"MENU_SEARCH",
																																						  "memoryUpdate":false,
																																						  "budget":500
																																						}

																																						User: Show menu of Green Bowl

																																						{
																																						  "intent":"RESTAURANT_MENU_QUERY",
																																						  "memoryUpdate":false,
																																						  "restaurantName":"Green Bowl"
																																						}

																																						Important:
																																						            - Return ONLY JSON.
																																						            - Do not wrap JSON in markdown.
																																						            - Do not explain anything.
																																						            - Use null for missing values.

																																		                User: What should I eat?

																																						{
  "intent":"RECOMMENDATION",
  "memoryUpdate":false,
  "dietType":null,
  "fitnessGoal":null,
  "budget":null,
  "foodItem":null,
  "restaurantName":null,
  "nutritionMetric":null,
  "sortOrder":null
}

																																						User: Recommend food under 300

																																						{
  "intent":"RECOMMENDATION",
  "memoryUpdate":false,
  "dietType":null,
  "fitnessGoal":null,
  "budget":300,
  "foodItem":null,
  "restaurantName":null,
  "nutritionMetric":null,
  "sortOrder":null
}

																																						User: Recommend meals for muscle gain

																																						{
  "intent":"RECOMMENDATION",
  "memoryUpdate":false,
  "dietType":null,
  "fitnessGoal":"Muscle Gain",
  "budget":null,
  "foodItem":null,
  "restaurantName":null,
  "nutritionMetric":"protein",
  "sortOrder":"DESC"
}

																																						User: Suggest a healthy vegetarian meal

																																						{
																																						  "intent":"RECOMMENDATION",
																																						  "memoryUpdate":false,
																																						  "dietType":"Vegetarian",
																																						  "fitnessGoal":null,
																																						  "budget":null,
																																						  "foodItem":null,
																																						  "restaurantName":null,
																																						  "nutritionMetric":null,
																                                                                                          "sortOrder":null
																																						}
																																						User: Which meal has highest protein?

																																		{
																																		  "intent":"NUTRITION_QUERY",
																																		  "memoryUpdate":false,
																																		  "dietType":null,
																																		  "fitnessGoal":null,
																																		  "budget":null,
																																		  "foodItem":null,
																																		  "restaurantName":null,
																																		  "nutritionMetric":"protein",
																                                                                          "sortOrder":"DESC"
																																		}

																																		User: Show low calorie meals

																																		{
																																		  "intent":"NUTRITION_QUERY",
																																		  "memoryUpdate":false,
																																		  "dietType":null,
																																		  "fitnessGoal":null,
																																		  "budget":null,
																																		  "foodItem":null,
																																		  "restaurantName":null,
																																		  "nutritionMetric":"calories",
												  "sortOrder":"ASC"
																																		}
																																		User: Which meal has highest calories?

												{
												  "intent":"NUTRITION_QUERY",
												  "memoryUpdate":false,
												  "dietType":null,
												  "fitnessGoal":null,
												  "budget":null,
												  "foodItem":null,
												  "restaurantName":null,
												  "nutritionMetric":"calories",
												  "sortOrder":"DESC"
												}

																																		User: Nutrition of Protein Shake

																																		{
																																		  "intent":"NUTRITION_QUERY",
																																		  "memoryUpdate":false,
																																		  "dietType":null,
																																		  "fitnessGoal":null,
																																		  "budget":null,
																																		  "foodItem":"Protein Shake",
																																		  "restaurantName":null,
																																		  "nutritionMetric":"protein",
												  "sortOrder":null
																																		}
																																		Nutrition Rules:

								highest protein
								most protein
								protein rich
								high protein
								muscle gain food
								gym food

								-> nutritionMetric=protein
								-> sortOrder=DESC

								highest calories
								calorie dense
								bulking food
								mass gain food

								-> nutritionMetric=calories
								-> sortOrder=DESC

								low calorie
								lowest calorie
								weight loss
								fat loss
								diet food

								-> nutritionMetric=calories
								-> sortOrder=ASC

								highest carbs
								most carbs

								-> nutritionMetric=carbs
								-> sortOrder=DESC																						User: Suggest vegetarian high protein meals under 300

																										{
				  "intent":"RECOMMENDATION",
				  "memoryUpdate":false,
				  "dietType":"Vegetarian",
				  "budget":300,
				  "nutritionMetric":"protein",
				  "sortOrder":"DESC",
				  "tools":[
				     "RECOMMENDATION",
				     "NUTRITION_QUERY"
				  ]
				}
																										User: Recommend low calorie meals

																						{
  "intent":"RECOMMENDATION",
  "memoryUpdate":false,
  "nutritionMetric":"calories",
  "sortOrder":"ASC",
  "tools":[
     "RECOMMENDATION",
     "NUTRITION_QUERY"
  ]
}
																		                User Request:
																						"""
																						+ userMessage;

		String response = llmProvider.getService().generate(prompt);

		System.out.println("AGENT ANALYSIS RAW = " + response);

		AgentAnalysis analysis = parse(response);

		cacheService.saveAnalysis(userMessage, analysis);

		return analysis;
	}

	private AgentAnalysis parse(String response) {

		response = response.trim();

		int start = response.indexOf("{");
		int end = response.lastIndexOf("}");

		if (start >= 0 && end >= 0) {
			response = response.substring(start, end + 1);
		}

		System.out.println("AGENT ANALYSIS JSON = " + response);

		JSONObject json = new JSONObject(response);

		AgentAnalysis analysis = new AgentAnalysis();

		if (!json.isNull("intent")) {
			analysis.setIntent(Intent.valueOf(json.getString("intent").trim().toUpperCase()));
		}

		analysis.setMemoryUpdate(json.optBoolean("memoryUpdate", false));

		if (!json.isNull("dietType")) {
			analysis.setDietType(json.getString("dietType"));
		}

		if (!json.isNull("fitnessGoal")) {
			analysis.setFitnessGoal(json.getString("fitnessGoal"));
		}

		if (!json.isNull("budget")) {
			analysis.setBudget(json.getInt("budget"));
		}

		if (!json.isNull("foodItem")) {
			analysis.setFoodItem(json.getString("foodItem"));
		}

		if (!json.isNull("restaurantName")) {
			analysis.setRestaurantName(json.getString("restaurantName"));
		}
		if (!json.isNull("nutritionMetric")) {
			analysis.setNutritionMetric(json.getString("nutritionMetric"));
		}
		if (!json.isNull("sortOrder")) {
			analysis.setSortOrder(json.getString("sortOrder"));
		}
		if (json.has("tools") && !json.isNull("tools")) {

			List<Intent> tools = new ArrayList<>();
			JSONArray array = json.getJSONArray("tools");

			for (int i = 0; i < array.length(); i++) {
				tools.add(Intent.valueOf(array.getString(i).trim().toUpperCase()));
			}
			analysis.setTools(tools);
		}

		return analysis;
	}
}