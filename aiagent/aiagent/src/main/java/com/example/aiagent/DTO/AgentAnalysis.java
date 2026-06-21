package com.example.aiagent.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AgentAnalysis {

    private Intent intent;

    private boolean memoryUpdate;

    private String dietType;

    private String fitnessGoal;

    private Integer budget;

    private String foodItem;

    private String restaurantName;
    
    private String nutritionGoal;
    
    private List<Intent> tools;
}