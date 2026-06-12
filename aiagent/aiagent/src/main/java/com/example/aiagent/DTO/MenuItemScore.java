package com.example.aiagent.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MenuItemScore {

    private MenuItem menuItem;
    private double score;

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public double getScore() {
        return score;
    }
}