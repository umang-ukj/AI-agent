package com.example.aiagent.tool;

import com.example.aiagent.DTO.Intent;

public interface Tool {

    Intent supportedIntent();

    String execute(String query);
}