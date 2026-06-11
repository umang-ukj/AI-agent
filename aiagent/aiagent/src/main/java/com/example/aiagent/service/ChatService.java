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
        
        Intent intent =intentClassifierService.classify(userMessage);
        
        String sessionId = chatRequest.getSessionId();
        
        //adding msg to in-memoryDB based on session ID
        memoryService.addMessage(sessionId,"user", userMessage);
        
        //convo history retrival based on sessionID
        List<String> history =memoryService.getConversation(sessionId);

        Tool tool =toolRegistry.getTool(intent);

        String toolContext = "";

        if(tool != null) {
            toolContext =tool.execute(userMessage);
        }
        
        if(toolContext.trim().equals("No matching restaurants found.")) {

            ChatResponse response =new ChatResponse();

            response.setResponse(toolContext);

            return response;
        }
        
     // Get summary
        String summary = memoryService.getSummary(sessionId);

        String fullPrompt;

        if(intent == Intent.RESTAURANT_SEARCH) {

            fullPrompt =
                    """
                    Use ONLY the restaurant data provided below.

                    Rules:
                    - Recommend restaurants only from the provided data.
                    - Do not invent restaurant names.
                    - If matching restaurants exist, recommend them.
                    - Mention their prices when relevant.

                    Restaurant Data:
                    """
                    + toolContext
                    + "\n\nUser Request:\n"
                    + userMessage;
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