package com.example.aiagent.tool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.AgentExecutionContext;
import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.ToolResult;
import com.example.aiagent.service.MenuItemService;

@Component
public class RecommendationTool implements Tool {

	@Autowired
	private MenuItemService menuItemService;

	@Override
	public Intent supportedIntent() {
		return Intent.RECOMMENDATION;
	}

	@Override
	public ToolResult execute(String query) {

		return new ToolResult("RecommendationTool", "Recommendation requires context");
	}

	@Override
	public ToolResult execute(String query, QueryContext context) {
		return execute(query, context, new AgentExecutionContext());
	}

	private String buildRecommendations(List<MenuItem> items) {

		StringBuilder sb = new StringBuilder();

		sb.append("Recommended Meals:\n\n");

		int count = 0;

		for (MenuItem item : items) {

			sb.append(count + 1).append(". ").append(item.getName()).append(" - Rs. ").append(item.getPrice())
					.append("\n");
			sb.append("Reason: ");

			String description = item.getDescription().toLowerCase();

			if (description.contains("protein")) {
				sb.append("High protein option");

			} else if (description.contains("healthy")) {
				sb.append("Healthy meal choice");
			} else {
				sb.append("Matches your preferences");
			}
			sb.append("\n\n");

			count++;

			if (count == 3) {
				break;
			}
		}
		return sb.toString();
	}

	@Override
	public ToolResult execute(String query, QueryContext context, AgentExecutionContext executionContext) {

		List<MenuItem> items = menuItemService.structuredSearch(context, null);
		if (items.isEmpty()) {
			return new ToolResult("RecommendationTool", "No recommendations found.");
		}
		executionContext.setCandidateMeals(items);
		System.out.println("Stored Candidate Meals = " + items.size());
		return new ToolResult("RecommendationTool", buildRecommendations(items));
	}
}