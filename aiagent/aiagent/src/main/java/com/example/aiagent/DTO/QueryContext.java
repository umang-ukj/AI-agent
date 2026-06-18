package com.example.aiagent.DTO;

public class QueryContext {

	private String foodItem;
	private String restaurantType;
	private Integer maxPrice;

	public String getFoodItem() {
		return foodItem;
	}

	public void setFoodItem(String foodItem) {
		this.foodItem = foodItem;
	}

	public String getRestaurantType() {
		return restaurantType;
	}

	public void setRestaurantType(String restaurantType) {
		this.restaurantType = restaurantType;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	@Override
	public String toString() {
		return "QueryContext [foodItem=" + foodItem + ", restaurantType=" + restaurantType + ", maxPrice=" + maxPrice
				+ "]";
	}
}