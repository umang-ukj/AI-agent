package com.example.aiagent.service;


import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SimilarityService {

    public double cosineSimilarity(List<Double> v1,List<Double> v2) {

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for(int i=0;i<v1.size();i++) {

            dot += v1.get(i) * v2.get(i);

            normA += v1.get(i) * v1.get(i);

            normB += v2.get(i) * v2.get(i);
        }

        return dot /(Math.sqrt(normA)* Math.sqrt(normB));
    }
}