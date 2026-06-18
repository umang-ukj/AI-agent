package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	private QueryUnderstandingService queryUnderstandingService;

	@Autowired
	private LLMIntentClassifierService llmIntentClassifierService;

	@Autowired
	private IntentClassifierService intentClassifierService;

	@Autowired
	private MemoryService memoryService;

	@Autowired
	private ToolRegistry toolRegistry;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemTool menuItemTool;

	public ChatResponse getResponse(ChatRequest chatRequest) {

		String userMessage = chatRequest.getMessage();

		String lower = userMessage.toLowerCase();

		String sessionId = chatRequest.getSessionId();

		ConversationContext context = memoryService.getContext(sessionId);

		QueryContext queryContext = queryUnderstandingService.extract(userMessage);

		System.out.println(queryContext);

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

		// Build memory context for intent classification
		Intent intent;

		if (context.getRestaurantName() != null && isMenuFollowUp(userMessage)) {
			System.out.println("FOLLOW UP MENU QUESTION DETECTED");
			intent = Intent.MENU_LOOKUP;

		} else {
			intent = intentClassifierService.classify(userMessage);

			if (intent == Intent.GENERAL_CHAT) {

				System.out.println("FALLING BACK TO LLM CLASSIFIER");

				intent = llmIntentClassifierService.classify(userMessage);
			}
		}

		if (intent == Intent.GENERAL_CHAT && intentClassifierService.isFollowUp(userMessage)) {

			String memoryContext = String.join(" ", history);

			intent = intentClassifierService.classify(memoryContext + " " + userMessage);
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

			if (intent == Intent.MENU_LOOKUP && context.getRestaurantName() != null) {

				Optional<Restaurant> restaurantOpt = restaurantRepository
						.findByNameContainingIgnoreCase(context.getRestaurantName());

				if (restaurantOpt.isPresent()) {

					toolContext = menuItemTool.execute(userMessage, restaurantOpt.get());

				} else {

					toolContext = ((Tool) tool).execute(userMessage);
				}

			} else {

				toolContext = ((Tool) tool).execute(userMessage);
			}

			System.out.println("TOOL = " + tool.getClass().getSimpleName());

			System.out.println("TOOL CONTEXT = [" + toolContext + "]");
		}

		// Save restaurant into conversation context
		if (toolContext.contains("Restaurant:")) {
			String[] lines = toolContext.split("\n");
			for (String line : lines) {
				if (line.startsWith("Restaurant:")) {

					String restaurantName = line.replace("Restaurant:", "").trim();

					context.setRestaurantName(restaurantName);

					memoryService.saveContext(sessionId, context);

					System.out.println("Saved Restaurant Context = " + restaurantName);

					break;
				}
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

	private boolean isMenuFollowUp(String message) {

		String lower = message.toLowerCase();

		return lower.contains("other") || lower.contains("more") || lower.contains("else") || lower.contains("dish")
				|| lower.contains("dishes") || lower.contains("food") || lower.contains("foods")
				|| lower.contains("meal") || lower.contains("meals") || lower.contains("item")
				|| lower.contains("items") || lower.contains("menu");
	}

	private boolean shouldSkipLLM(Intent intent) {

		return intent == Intent.RESTAURANT_SEARCH || intent == Intent.MENU_SEARCH || intent == Intent.PRICE_LOOKUP
				|| intent == Intent.RESTAURANT_MENU_QUERY || intent == Intent.MENU_LOOKUP;
	}
}