package com.example.aiagent.tool;

import com.example.aiagent.DTO.AgentExecutionContext;
import com.example.aiagent.DTO.Intent;
import com.example.aiagent.DTO.QueryContext;
import com.example.aiagent.DTO.ToolResult;

public interface Tool {

	Intent supportedIntent();

	ToolResult execute(String query);

	default ToolResult execute(String query, QueryContext context) {
		return execute(query);
	}

	default ToolResult execute(String query, QueryContext queryContext, AgentExecutionContext executionContext) {
		return execute(query, queryContext);
	}
}