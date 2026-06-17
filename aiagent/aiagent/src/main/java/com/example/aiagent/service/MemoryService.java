package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.ConversationContext;

//to store conversations in in-memory DB temporarily till we work on redis DB
//stores conversation history based on sessionId. we can retrieve chats also based on sessionId

@Service
public class MemoryService {
	@Autowired
	private SummarizationService summarizationService;
	private final Map<String, List<String>> memory = new HashMap<>();
	private final Map<String, String> summaries = new HashMap<>();
	private final Map<String, ConversationContext> contexts = new ConcurrentHashMap<>();

	public void addMessage(String sessionId, String role, String message) {
		memory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(role + ": " + message);
		List<String> messages = memory.get(sessionId);

		if (messages.size() > 10) {

			// summarizing msgs
			String summary = summarizationService.summarize(messages);

			saveSummary(sessionId, summary);

			// summary + recent messages
			memory.put(sessionId, new ArrayList<>(messages.subList(messages.size() - 4, messages.size())));
		}
	}

	public List<String> getConversation(String sessionId) {

		List<String> history = memory.getOrDefault(sessionId, new ArrayList<>());

		if (history.size() > 6) {
			return new ArrayList<>(history.subList(history.size() - 6, history.size()));
		}
		return history;
	}

	// to get summary
	public String getSummary(String sessionId) {
		return summaries.getOrDefault(sessionId, "");
	}

	// to store summary
	public void saveSummary(String sessionId, String summary) {
		summaries.put(sessionId, summary);
	}

	public ConversationContext getContext(String sessionId) {

		ConversationContext context = contexts.computeIfAbsent(sessionId, id -> new ConversationContext());

		if (context.getLastUpdated() != null) {

			long minutes = java.time.Duration.between(context.getLastUpdated(), java.time.LocalDateTime.now())
					.toMinutes();

			if (minutes > 10) {

				System.out.println("Conversation Context Expired");

				context.setRestaurantName(null);
				context.setMenuItemName(null);
				context.setLastUpdated(null);
			}
		}

		return context;
	}

	public void saveContext(String sessionId, ConversationContext context) {

		context.setLastUpdated(java.time.LocalDateTime.now());

		contexts.put(sessionId, context);
	}

}
