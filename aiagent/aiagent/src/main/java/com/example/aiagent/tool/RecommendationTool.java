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

	@Override
	public ToolResult execute(String query, QueryContext context, AgentExecutionContext executionContext) {

		List<MenuItem> items = menuItemService.structuredSearch(context, null);
		if (items.isEmpty()) {
			return new ToolResult("RecommendationTool", "No recommendations found.");
		}

		executionContext.setCandidateMeals(items);
		System.out.println("Stored Candidate Meals = " + items.size());
		return new ToolResult("RecommendationTool", "Found " + items.size() + " recommendations", items);
	}
}