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
public class QueryContext {

	private String foodItem;
	private String restaurantType;
	private Integer maxPrice;
	private String nutritionMetric;
	private String sortOrder;
	private String restaurantName;


}