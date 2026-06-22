package com.example.aiagent.DTO;

public class ToolResult {

	private String toolName;
	private String content;

	public ToolResult() {
	}

	public ToolResult(String toolName, String content) {
		this.toolName = toolName;
		this.content = content;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}