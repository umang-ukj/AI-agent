package com.example.aiagent.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationContext {

	private String restaurantName;
	private String menuItemName;
	private LocalDateTime lastUpdated;
	private String dietType;
	private String fitnessGoal;
	private Integer budget;
}