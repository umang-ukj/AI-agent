package com.example.aiagent.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.aiagent.DTO.MenuItem;
import com.example.aiagent.DTO.MenuItemScore;
import com.example.aiagent.repository.MenuItemRepository;

public class MenuItemService {
	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private EmbeddingService embeddingService;

	@Autowired
	private SimilarityService similarityService;
	
	public List<MenuItem> semanticSearch(String query) {

	    List<Double> queryEmbedding =embeddingService.generateEmbedding(query);

	    List<MenuItemScore> scores =new ArrayList<>();

	    List<MenuItem> menuItems =menuItemRepository.findAll();

	    for(MenuItem item : menuItems) {

	        List<Double> itemEmbedding =embeddingService.generateEmbedding(item.getDescription());

	        double similarity =similarityService.cosineSimilarity(queryEmbedding,itemEmbedding);

	        scores.add(new MenuItemScore(item,similarity));
	    }

	    scores.sort((a,b) ->Double.compare(b.getScore(),a.getScore()));

	    return scores.stream().limit(3).map(MenuItemScore::getMenuItem).toList();
	}

}
