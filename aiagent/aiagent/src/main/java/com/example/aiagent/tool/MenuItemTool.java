package com.example.aiagent.tool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.service.MenuItemService;
import com.example.aiagent.service.QueryUnderstandingService;

@Component
public class MenuItemTool implements Tool {

	@Autowired
	private MenuItemService menuItemService;

	@Autowired
	private QueryUnderstandingService queryUnderstandingService;

	@Override
	public Intent supportedIntent() {
		return Intent.MENU_SEARCH;
	}

	@Override
	public String execute(String query) {

		QueryContext context = queryUnderstandingService.extract(query);

		List<MenuItem> items = menuItemService.structuredSearch(context, null);

		if (items.isEmpty()) {

			return "No matching menu item found.";
		}

		return buildResponse(items);
	}

	public String execute(String query, Restaurant restaurant) {

		return buildResponse(menuItemService.semanticSearch(query, restaurant));
	}

	private String buildResponse(List<MenuItem> items) {

		if (items.isEmpty()) {
			return "No matching menu items found.";
		}

		StringBuilder menuData = new StringBuilder();

		for (MenuItem item : items) {

			menuData.append("Restaurant: ").append(item.getRestaurant().getName()).append("\n").append("Name: ")
					.append(item.getName()).append(", Price: ").append(item.getPrice()).append(", Description: ")
					.append(item.getDescription()).append("\n");
		}

		return menuData.toString();
	}

	@Override
	public String execute(String query, QueryContext context) {

		List<MenuItem> items = menuItemService.structuredSearch(context, null);

		if (items.isEmpty()) {
			return "No matching menu item found.";
		}

		return buildResponse(items);
	}

}
