package com.example.aiagent.tool;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.AgentExecutionContext;
import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.ToolResult;
import com.example.aiagent.service.MenuItemService;

@Component
public class NutritionTool implements Tool {

	@Autowired
	private MenuItemService menuItemService;

	@Override
	public Intent supportedIntent() {
		return Intent.NUTRITION_QUERY;
	}

	@Override
	public ToolResult execute(String query) {
		return execute(query, null);
	}

	private ToolResult buildNutritionResult(String query, QueryContext context, List<MenuItem> items) {

		MenuItem bestMeal = processNutrition(query, context, items);
		if (bestMeal == null) {
			return new ToolResult("Nutrition Tool", """
					Try asking:

					- Which meal has highest protein?
					- Show low calorie meals
					- Nutrition of Protein Shake
					""");
		}

		return new ToolResult("Nutrition Tool", "Best nutrition match found", bestMeal);
	}

	@Override
	public ToolResult execute(String query, QueryContext context) {
		List<MenuItem> items = menuItemService.structuredSearch(context, null);

		return buildNutritionResult(query, context, items);
	}

	private static final Map<String, ToDoubleFunction<MenuItem>> METRICS = Map.of("protein", item -> item.getProtein(),
			"calories", item -> item.getCalories(), "carbs", item -> item.getCarbs(), "fat", item -> item.getFat());

	@Override
	public ToolResult execute(String query, QueryContext context, AgentExecutionContext executionContext) {

		List<MenuItem> items;

		if (executionContext.hasCandidateMeals()) {
			System.out.println("NutritionTool using chained candidates");
			items = executionContext.getCandidateMeals();
		} else {
			System.out.println("NutritionTool using structured search");
			items = menuItemService.structuredSearch(context, null);
		}

		return buildNutritionResult(query, context, items);
	}

	private MenuItem processNutrition(String query, QueryContext context, List<MenuItem> items) {

		String lower = query.toLowerCase();

		String metric = context != null ? context.getNutritionMetric() : null;

		String sortOrder = context != null ? context.getSortOrder() : null;

		if (metric != null) {
			ToDoubleFunction<MenuItem> extractor = METRICS.get(metric.toLowerCase());

			if (extractor != null) {
				Comparator<MenuItem> comparator = Comparator.comparingDouble(extractor);
				MenuItem best = "DESC".equalsIgnoreCase(sortOrder) ? items.stream().max(comparator).orElse(null)
						: items.stream().min(comparator).orElse(null);

				return best;
			}
		}

		for (MenuItem item : items) {

			if (lower.contains(item.getName().toLowerCase())) {
				return item;
			}
		}

		return null;
	}
}