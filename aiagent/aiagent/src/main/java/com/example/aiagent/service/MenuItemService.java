package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.MenuItemScore;
import com.example.aiagent.repository.MenuItemRepository;
@Service
public class MenuItemService {
	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private EmbeddingService embeddingService;

	@Autowired
	private SimilarityService similarityService;
	
	public List<MenuItem> semanticSearch(String query) {

	    String lowerQuery = query.toLowerCase();

	    List<MenuItem> menuItems =menuItemRepository.findAll();

	    // HARD FILTERS FIRST

	    if(lowerQuery.contains("non veg")|| lowerQuery.contains("non-veg")
	            || lowerQuery.contains("chicken")|| lowerQuery.contains("meat")) {

	        menuItems =menuItems.stream().filter(item ->item.getDescription().toLowerCase().contains("chicken")
	                        ||item.getDescription().toLowerCase().contains("meat")).toList();
	    }

	    if(lowerQuery.contains("veg")|| lowerQuery.contains("vegetarian")) {

	        menuItems =menuItems.stream().filter(item ->item.getDescription().toLowerCase().contains("vegetarian")).toList();
	    }

	    List<Double> queryEmbedding =embeddingService.generateEmbedding(query);

	    List<MenuItemScore> scores =new ArrayList<>();

	    for(MenuItem item : menuItems) {

	        List<Double> itemEmbedding =embeddingService.fromString(item.getEmbedding());

	        double similarity =similarityService.cosineSimilarity(queryEmbedding,itemEmbedding);

	        System.out.println(item.getName()+ " -> "+ similarity);

	        scores.add(new MenuItemScore(item,similarity));
	    }

	    scores.sort((a,b) ->Double.compare(b.getScore(),a.getScore()));

	    return scores.stream().limit(3).map(MenuItemScore::getMenuItem).toList();
	}

}
