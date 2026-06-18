package com.example.aiagent.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAiLLMService implements LLMService {

	@Value("${openai.api.key}")
	private String apiKey;

	@Value("${openai.model}")
	private String model;

	private final RestTemplate restTemplate = new RestTemplate();

	public String generate(String prompt) {

		String url = "https://api.openai.com/v1/chat/completions";

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of("model", model, "messages",
				List.of(Map.of("role", "user", "content", prompt)));

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		Map response = restTemplate.postForObject(url, request, Map.class);

		List choices = (List) response.get("choices");

		Map choice = (Map) choices.get(0);

		Map message = (Map) choice.get("message");

		return message.get("content").toString();
	}

	public String generate(String prompt, double temperature) {

		String url = "https://api.openai.com/v1/chat/completions";

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of("model", model, "temperature", temperature, "messages",
				List.of(Map.of("role", "user", "content", prompt)));

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		Map response = restTemplate.postForObject(url, request, Map.class);

		List choices = (List) response.get("choices");

		Map choice = (Map) choices.get(0);

		Map message = (Map) choice.get("message");

		return message.get("content").toString();
	}
}