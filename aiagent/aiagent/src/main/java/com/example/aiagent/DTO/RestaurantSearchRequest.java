package com.example.aiagent.DTO;


public class RestaurantSearchRequest {

    private Integer budget;
    private String type;

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}