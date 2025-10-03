package com.rozynlp.informationextractor.controller;

import com.rozynlp.informationextractor.service.KnowledgeGraphService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/information-extractor/graph")
public class KnowledgeGraphController {

    private final KnowledgeGraphService service;

    public KnowledgeGraphController(KnowledgeGraphService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, Object> extractGraph(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        return service.extractKnowledge(text);
    }
}
