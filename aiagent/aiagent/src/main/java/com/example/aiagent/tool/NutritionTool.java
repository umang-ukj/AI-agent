package com.example.aiagent.tool;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
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
	public String execute(String query) {
		return execute(query, null);
	}

	@Override
	public String execute(String query, QueryContext context) {

		String lower = query.toLowerCase();

		List<MenuItem> items = menuItemService.getAllMenuItems();

		String nutritionGoal = context.getNutritionGoal();

		if ("HIGH_PROTEIN".equals(nutritionGoal)) {

			MenuItem best = items.stream().max(Comparator.comparing(MenuItem::getProtein)).orElse(null);

			return buildNutritionResponse(best);
		}

		if ("LOW_CALORIE".equals(nutritionGoal)) {

			MenuItem best = items.stream().min(Comparator.comparing(MenuItem::getCalories)).orElse(null);

			return buildNutritionResponse(best);
		}

		// Item specific

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
}