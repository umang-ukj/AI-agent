package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.AgentAnalysis;
import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;
import com.example.aiagent.DTO.ConversationContext;
import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.repository.RestaurantRepository;
import com.example.aiagent.tool.MenuItemTool;
import com.example.aiagent.tool.Tool;

@Service
public class ChatService {

	@Autowired
	private LLMProvider llmProvider;

	@Autowired
	private MemoryService memoryService;

	@Autowired
	private ToolRegistry toolRegistry;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemTool menuItemTool;

	@Autowired
	private AgentAnalysisService agentAnalysisService;

	public ChatResponse getResponse(ChatRequest chatRequest) {

		String userMessage = chatRequest.getMessage();

		AgentAnalysis analysis = agentAnalysisService.analyze(userMessage);

		System.out.println("AGENT ANALYSIS = " + analysis);

		String lower = userMessage.toLowerCase();

		String sessionId = chatRequest.getSessionId();

		ConversationContext context = memoryService.getContext(sessionId);

		boolean preferenceUpdated = false;

		if (analysis.isMemoryUpdate()) {

			if (analysis.getDietType() != null) {
				context.setDietType(analysis.getDietType());
				preferenceUpdated = true;
			}

			if (analysis.getFitnessGoal() != null) {
				context.setFitnessGoal(analysis.getFitnessGoal());
				preferenceUpdated = true;
			}

			if (analysis.getBudget() != null) {
				context.setBudget(analysis.getBudget());
				preferenceUpdated = true;
			}
		}

		if (preferenceUpdated) {

			memoryService.saveContext(sessionId, context);

			System.out.println("Stored Diet = " + context.getDietType());
			System.out.println("Stored Goal = " + context.getFitnessGoal());
			System.out.println("Stored Budget = " + context.getBudget());

			ChatResponse response = new ChatResponse();

			StringBuilder ack = new StringBuilder("Got it. I'll remember");

			if (analysis.getDietType() != null) {
				ack.append(" that you prefer ").append(analysis.getDietType().toLowerCase()).append(" food");
			}

			if (analysis.getFitnessGoal() != null) {
				ack.append(". I'll prioritize ").append(analysis.getFitnessGoal().toLowerCase()).append(" meals");
			}

			if (analysis.getBudget() != null) {
				ack.append(". Budget noted: ").append(analysis.getBudget());
			}

			response.setResponse(ack.toString());

			return response;
		}

		QueryContext queryContext = new QueryContext();

		queryContext.setFoodItem(analysis.getFoodItem());

		queryContext.setRestaurantType(analysis.getDietType());

		queryContext.setMaxPrice(analysis.getBudget());

		System.out.println(queryContext);

		if (queryContext.getRestaurantType() == null && context.getDietType() != null) {

			queryContext.setRestaurantType(context.getDietType());
		}

		if (queryContext.getMaxPrice() == null && context.getBudget() != null) {

			queryContext.setMaxPrice(context.getBudget());
		}

		System.out.println("Final QueryContext = " + queryContext);

		// Resolve follow-up references using conversation context

		if (lower.contains("it") && context.getRestaurantName() != null) {
			userMessage = userMessage.replace("it", context.getRestaurantName());
			lower = userMessage.toLowerCase();
		}

		if (lower.contains("them") && context.getRestaurantName() != null) {
			userMessage = userMessage.replace("them", context.getRestaurantName());
			lower = userMessage.toLowerCase();
		}

		// Get history BEFORE adding current message
		List<String> history = memoryService.getConversation(sessionId);

		String summary = memoryService.getSummary(sessionId);

		if (history.size() > 6) {

			List<String> oldMessages = new ArrayList<>(history.subList(0, history.size() - 6));

			String summaryText = String.join("\n", oldMessages);

			summary = summary + "\n" + summaryText;

			memoryService.saveSummary(sessionId, summary);

			history = new ArrayList<>(history.subList(history.size() - 6, history.size()));
		}

		Intent intent;

		intent = analysis.getIntent();

		if (intent == Intent.GENERAL_CHAT && context.getRestaurantName() != null
				&& isRestaurantMenuFollowUp(userMessage)) {

			intent = Intent.RESTAURANT_MENU_QUERY;
		}

		System.out.println("INTENT = " + intent);

		// Now save current user message
		memoryService.addMessage(sessionId, "user", userMessage);

		Object tool = toolRegistry.getTool(intent);

		if (tool == null) {

			System.out.println("NO TOOL FOUND FOR INTENT = " + intent);

			String aiText = llmProvider.getService().generate(userMessage);

			memoryService.addMessage(sessionId, "AI", aiText);

			ChatResponse response = new ChatResponse();

			response.setResponse(aiText);

			return response;
		}

		String toolContext = "";

		if (tool != null) {

			if (intent == Intent.RESTAURANT_MENU_QUERY && context.getRestaurantName() != null) {

				Optional<Restaurant> restaurantOpt = restaurantRepository
						.findByNameContainingIgnoreCase(context.getRestaurantName());

				if (restaurantOpt.isPresent()) {

					toolContext = menuItemTool.execute(userMessage, restaurantOpt.get());

				} else {

					toolContext = ((Tool) tool).execute(userMessage, queryContext);
				}

			} else {

				toolContext = ((Tool) tool).execute(userMessage, queryContext);
			}

			System.out.println("TOOL = " + tool.getClass().getSimpleName());

			System.out.println("TOOL CONTEXT = [" + toolContext + "]");
		}

		// Save restaurant into conversation context
		// Save restaurant context only when exactly one restaurant is returned
		if (toolContext.contains("Restaurant:")) {

			List<String> restaurants = new ArrayList<>();

			String[] lines = toolContext.split("\n");

			for (String line : lines) {

				if (line.startsWith("Restaurant:")) {

					String restaurantName = line.replace("Restaurant:", "").trim();

					if (!restaurants.contains(restaurantName)) {
						restaurants.add(restaurantName);
					}
				}
			}

			if (restaurants.size() == 1) {

				String restaurantName = restaurants.get(0);

				context.setRestaurantName(restaurantName);

				memoryService.saveContext(sessionId, context);

				System.out.println("Saved Restaurant Context = " + restaurantName);

			} else {

				System.out.println("Skipping restaurant context save. Restaurants found = " + restaurants.size());
			}
		}

		if (toolContext.startsWith("No matching")) {

			ChatResponse response = new ChatResponse();
			response.setResponse(toolContext);

			return response;
		}

		if (shouldSkipLLM(intent)) {

			System.out.println("RETURNING TOOL RESPONSE DIRECTLY");

			memoryService.addMessage(sessionId, "AI", toolContext);

			ChatResponse response = new ChatResponse();
			response.setResponse(toolContext);

			return response;
		}

		// Get summary
		// String summary = memoryService.getSummary(sessionId);

		System.out.println("\n=== HISTORY ===");
		history.forEach(System.out::println);

		System.out.println("\n=== SUMMARY ===");
		System.out.println(summary);
		String fullPrompt;

		if (intent == Intent.RESTAURANT_SEARCH) {

			fullPrompt = """
					Use ONLY the restaurant data below.

					Conversation Summary:
					""" + summary + """

					Recent Conversation:
					""" + String.join("\n", history) + """

					Current User Request:
					""" + userMessage + """

					Restaurant Data:
					""" + toolContext + """

					If the user asks for recommendations,
					recommend restaurants.

					If the user asks about a specific restaurant,
					answer only that question.

					Keep the answer under 3 lines.

					Do not invent restaurants.
					Do not invent prices.
					Do not add introductions.
					Do not add conclusions.
					""";

		} else if (intent == Intent.MENU_SEARCH) {

			fullPrompt = """
					Use ONLY the menu items provided below.

					Conversation Summary:
					""" + summary + """

					Recent Conversation:
					""" + String.join("\n", history) + """

					Current User Request:
					""" + userMessage + """

					Menu Data:
					""" + toolContext + """

					Return at most 3 matching items.

					Format:
					1. Item Name - Price - short reason

					Do not invent items.
					Do not invent prices.
					Do not add introductions.
					Do not add conclusions.
					""";
		} else {

			fullPrompt = """
					Use the conversation summary and history as context.

					Rules:
					- Answer only the latest user question.
					- Do not repeat the summary.
					- Do not summarize the conversation.

					Summary:
					""" + summary + "\n\n" + String.join("\n", history);
		}

		System.out.println("\n========== PROMPT ==========");
		System.out.println(fullPrompt);
		System.out.println("============================\n");

		String aiText = llmProvider.getService().generate(fullPrompt);
		memoryService.addMessage(sessionId, "AI", aiText);

		// Return DTO
		ChatResponse chatResponse = new ChatResponse();
		chatResponse.setResponse(aiText);

		return chatResponse;
	}

	private boolean isRestaurantMenuFollowUp(String message) {

		String lower = message.toLowerCase();

		return lower.contains("other dishes") || lower.contains("more dishes") || lower.contains("full menu")
				|| lower.contains("what else") || lower.contains("show menu");
	}

	private boolean shouldSkipLLM(Intent intent) {

		return intent == Intent.RESTAURANT_SEARCH || intent == Intent.MENU_SEARCH
				|| intent == Intent.RESTAURANT_MENU_QUERY;
	}
}