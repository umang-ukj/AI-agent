package com.example.aiagent.tool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.Restaurant;
import com.example.aiagent.DTO.ToolResult;
import com.example.aiagent.service.MenuItemService;

@Component
public class MenuItemTool implements Tool {

	@Autowired
	private MenuItemService menuItemService;

	@Override
	public Intent supportedIntent() {
		return Intent.MENU_SEARCH;
	}

	@Override
	public ToolResult execute(String query) {

		return execute(query, new QueryContext());
	}

	public ToolResult execute(String query, Restaurant restaurant) {
		List<MenuItem> items = menuItemService.semanticSearch(query, restaurant);

		if (items.isEmpty()) {
			return new ToolResult("MenuItem Tool", "No matching menu items found.");
		}

		return new ToolResult("MenuItem Tool", "Found " + items.size() + " menu items", items);
	}

	@Override
	public ToolResult execute(String query, QueryContext context) {

		List<MenuItem> items = menuItemService.structuredSearch(context, null);
		if (items.isEmpty()) {
			return new ToolResult("MenuItem Tool", "No matching menu item found.");
		}
		return new ToolResult("MenuItem Tool", "Found " + items.size() + " menu items", items);
	}

}
