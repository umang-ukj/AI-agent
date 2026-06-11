package com.example.aiagent.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


// to summarize messages
@Service
public class SummarizationService {

	@Autowired
	private LLMService llmService;

    public String summarize(List<String> messages) {

        String conversation =String.join("\n", messages);

        String prompt =
        	    """
        	    Summarize the following conversation.

        	    Rules:
        	    - Maximum 2 lines
        	    - No introduction
        	    - No phrases like "Here is a summary"
        	    - Return only the summary

        	    Conversation:
        	    """
        	    + conversation;

        
        return llmService.generate(prompt);
    }
}