package com.example.aiagent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMProvider {

	@Autowired
	private OpenAiLLMService openAi;

	@Autowired
	private OllamaLLMService ollama;

	@Value("${llm.provider}")
	private String provider;

	public LLMService getService() {

		if ("openai".equalsIgnoreCase(provider)) {
			return openAi;
		}

		return ollama;
	}
}