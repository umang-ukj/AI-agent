package com.example.aiagent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.Restaurant;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
	List<MenuItem> findByRestaurant(Restaurant restaurant);
}