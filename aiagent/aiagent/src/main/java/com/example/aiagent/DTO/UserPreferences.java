package com.example.aiagent.DTO;

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
public class UserPreferences {

    private String dietType;

    private String fitnessGoal;

    private Integer budget;

}
