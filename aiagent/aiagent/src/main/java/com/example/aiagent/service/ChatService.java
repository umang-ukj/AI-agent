package com.example.aiagent.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.aiagent.DTO.ChatRequest;
import com.example.aiagent.DTO.ChatResponse;
import com.example.aiagent.DTO.Intent;
import com.example.aiagent.tool.Tool;
@Service
public class ChatService {

	@Autowired
	private LLMService llmService;
    
    @Autowired
    private IntentService intentService;
    
    @Autowired
    private IntentClassifierService intentClassifierService;
    
    @Autowired
    private MemoryService memoryService;
    
    @Autowired
    private ToolRegistry toolRegistry;

    public ChatResponse getResponse(ChatRequest chatRequest) {

        String userMessage = chatRequest.getMessage();
        
        String sessionId = chatRequest.getSessionId();

        // Get history BEFORE adding current message
        List<String> history = memoryService.getConversation(sessionId);

        if(history.size() > 6) {
            history = history.subList(history.size() - 6,history.size());
        }

         // Build memory context for intent classification
        Intent intent =intentClassifierService.classify(userMessage);

        if(intent == Intent.GENERAL_CHAT&& intentClassifierService.isFollowUp(userMessage)) {

            String memoryContext =String.join(" ", history);

            intent =intentClassifierService.classify(memoryContext + " " + userMessage);
        }

     System.out.println("INTENT = " + intent);

     // Now save current user message
     memoryService.addMessage(sessionId,"user",userMessage);

        Object tool = toolRegistry.getTool(intent);

        if(tool == null) {

            String aiText =llmService.generate(userMessage);

            memoryService.addMessage(sessionId,"AI",aiText);

            ChatResponse response =new ChatResponse();

            response.setResponse(aiText);

            return response;
        }
        
        String toolContext = "";

        if(tool != null) {
            toolContext =((Tool) tool).execute(userMessage);
        }
        
        if(toolContext.startsWith("No matching")) {

            ChatResponse response = new ChatResponse();
            response.setResponse(toolContext);
            return response;
        }
        
        // Get summary
        String summary = memoryService.getSummary(sessionId);
        
        System.out.println("\n=== HISTORY ===");
        history.forEach(System.out::println);

        System.out.println("\n=== SUMMARY ===");
        System.out.println(summary);
        String fullPrompt;

        if(intent == Intent.RESTAURANT_SEARCH) {

        	fullPrompt =
        	        """
        	        Use ONLY the restaurant data below.

        	        Current User Request:
        	        """
        	        + userMessage
        	        + """

        	        Restaurant Data:
        	        """
        	        + toolContext
        	        + """

        	        If the user asks for recommendations,
        	        recommend restaurants.

        	        If the user asks about a specific restaurant,
        	        answer only that question.

        	        Keep the answer under 3 lines.

        	        Do not invent restaurants.
        	        Do not invent prices.
        	        Do not add introductions.
        	        Do not add conclusions.
        	        """;

        }
        else if(intent == Intent.MENU_SEARCH) {

        	fullPrompt =
        	        """
        	        Use ONLY the menu items provided below.

        	        Current User Request:
        	        """
        	        + userMessage
        	        + """

        	        Menu Data:
        	        """
        	        + toolContext
        	        + """

        	        Return at most 3 matching items.

        	        Format:
        	        1. Item Name - Price - short reason

        	        Do not invent items.
        	        Do not invent prices.
        	        Do not add introductions.
        	        Do not add conclusions.
        	        """;
        	}
        else {

            fullPrompt =
                    """
                    Use the conversation summary and history as context.

                    Rules:
                    - Answer only the latest user question.
                    - Do not repeat the summary.
                    - Do not summarize the conversation.

                    Summary:
                    """
                    + summary
                    + "\n\n"
                    + String.join("\n", history);
        }

        System.out.println("\n========== PROMPT ==========");
        System.out.println(fullPrompt);
        System.out.println("============================\n");
        
        
        String aiText =llmService.generate(fullPrompt);
        memoryService.addMessage(sessionId,"AI", aiText);

        // Return DTO
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setResponse(aiText);

        return chatResponse;
    }
}