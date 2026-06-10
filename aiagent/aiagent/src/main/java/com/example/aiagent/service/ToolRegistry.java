package com.example.aiagent.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.Intent;
import com.example.aiagent.tool.Tool;

@Service
public class ToolRegistry {

    private final Map<Intent, Tool> tools =new HashMap<>();

    @Autowired
    public ToolRegistry(List<Tool> toolList) {

        for(Tool tool : toolList) {
            tools.put(tool.supportedIntent(),tool);
        }
    }

    public Tool getTool(Intent intent) {
        return tools.get(intent);
    }
}
