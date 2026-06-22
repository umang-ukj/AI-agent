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

	@Override
	public ToolResult execute(String query, QueryContext context) {
		return new ToolResult("Nutrition Tool", processNutrition(query, context, menuItemService.getAllMenuItems()));
	}

	private static final Map<String, ToDoubleFunction<MenuItem>> METRICS = Map.of("protein", item -> item.getProtein(),
			"calories", item -> item.getCalories(), "carbs", item -> item.getCarbs(), "fat", item -> item.getFat());

	private String buildNutritionResponse(MenuItem item) {

		if (item == null) {
			return "Nutrition data not found.";
		}

		return String.format("""
				%s

				Protein : %dg
				Calories : %d
				Carbs : %dg
				Fat : %dg
				""", item.getName(), item.getProtein(), item.getCalories(), item.getCarbs(), item.getFat());
	}

	@Override
	public ToolResult execute(String query, QueryContext context, AgentExecutionContext executionContext) {

		String lower = query.toLowerCase();

		List<MenuItem> items;

		if (executionContext.getCandidateMeals() != null && !executionContext.getCandidateMeals().isEmpty()) {
			items = executionContext.getCandidateMeals();

			System.out.println("NutritionTool using chained candidates");
		} else {
			items = menuItemService.getAllMenuItems();
			System.out.println("NutritionTool using full database");
		}

		return new ToolResult("Nutrition Tool", processNutrition(lower, context, items));
	}

	private String processNutrition(String query, QueryContext context, List<MenuItem> items) {

		String lower = query.toLowerCase();
		String metric = context.getNutritionMetric();
		String sortOrder = context.getSortOrder();

		if (metric != null) {
			ToDoubleFunction<MenuItem> extractor = METRICS.get(metric.toLowerCase());

			if (extractor != null) {
				Comparator<MenuItem> comparator = Comparator.comparingDouble(extractor);
				MenuItem best = "DESC".equalsIgnoreCase(sortOrder) ? items.stream().max(comparator).orElse(null)
						: items.stream().min(comparator).orElse(null);

				return buildNutritionResponse(best);
			}
		}

		for (MenuItem item : items) {

			if (lower.contains(item.getName().toLowerCase())) {

				return buildNutritionResponse(item);
			}
		}

		return """
				Try asking:

				- Which meal has highest protein?
				- Show low calorie meals
				- Nutrition of Protein Shake
				""";
	}
}