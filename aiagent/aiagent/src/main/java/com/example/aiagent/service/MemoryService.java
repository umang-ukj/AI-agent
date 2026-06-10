package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

//to store conversations in in-memory DB temporarily till we work on redis DB
//stores conversation history based on sessionId. we can retrieve chats also based on sessionId

@Service
public class MemoryService {
	
	private final Map<String, List<String>> memory=new HashMap<>();
	
	public void addMessage(String sessionId, String role, String message) {
		memory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(role + ": " + message);
	}
	
	public List<String> getConversation(String sessionId) {

	    List<String> history =memory.getOrDefault(sessionId,new ArrayList<>());

	    if(history.size() > 6) {
	        return history.subList(history.size() - 6,history.size());
	    }
	    
	    return history;
	}

}
