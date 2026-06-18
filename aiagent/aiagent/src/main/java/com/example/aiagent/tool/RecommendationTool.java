package com.example.aiagent.tool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
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
	public String execute(String query) {
		return "Recommendation requires context";
	}

	@Override
	public String execute(String query, QueryContext context) {

		List<MenuItem> items = menuItemService.structuredSearch(context, null);

		if (items.isEmpty()) {
			return "No recommendations found.";
		}

		return buildRecommendations(items);
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
}