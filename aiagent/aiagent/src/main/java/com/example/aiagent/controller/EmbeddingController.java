package com.example.aiagent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aiagent.service.EmbeddingService;
import com.example.aiagent.service.SimilarityService;

@RestController
@RequestMapping("/embedding")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private SimilarityService similarityService;

    @GetMapping
    public Object test(@RequestParam String text) {

        return embeddingService.generateEmbedding(text);
    }
    
    @GetMapping("/similarity")
    public double similarity() {

        List<Double> e1 =embeddingService.generateEmbedding("Healthy vegetarian meals rich in protein");

        List<Double> e2 =embeddingService.generateEmbedding("Protein rich gym food");

        return similarityService.cosineSimilarity(e1, e2);
    }
    
}