package com.example.aiagent.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Restaurant {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private int price;
    private String description;
    @Column(columnDefinition = "LONGTEXT")
    private String embedding;
    
    @Override
    public String toString() {
        return "Name: " + name +", Type: " + type +", Price: " + price+", Description: " + description;
    }
}
