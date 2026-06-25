package com.example.aiagent.DTO;

public class ToolResult {

	private String toolName;
	private String message;
	private Object data;

	public ToolResult() {
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ToolResult(String toolName, String message, Object data) {
		super();
		this.toolName = toolName;
		this.message = message;
		this.data = data;
	}

	public ToolResult(String toolName, String message) {
		this.toolName = toolName;
		this.message = message;
	}
}