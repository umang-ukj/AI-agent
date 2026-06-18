package com.example.aiagent.tool;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.QueryContext;

public interface Tool {

	Intent supportedIntent();

	String execute(String query);

	default String execute(String query, QueryContext context) {
		return execute(query);
	}
}