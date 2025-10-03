package com.rozynlp.informationextractor.controller;

import com.rozynlp.informationextractor.service.KnowledgeGraphNeo4jService;
import com.rozynlp.informationextractor.service.KnowledgeGraphNeo4jService.Neo4jTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/information-extractor")
public class KnowledgeGraphNeo4jController {

    private final KnowledgeGraphNeo4jService knowledgeGraphNeo4jService;

    @Autowired
    public KnowledgeGraphNeo4jController(KnowledgeGraphNeo4jService knowledgeGraphNeo4jService) {
        this.knowledgeGraphNeo4jService = knowledgeGraphNeo4jService;
    }

    // Input DTO for JSON input
    public static class InputRequest {
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    // Output DTO for each Neo4j triple
    public static class TripleResponse {
        private String subject;
        private String predicate;
        private String object;
        public TripleResponse(String subject, String predicate, String object) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
        }
        public String getSubject() { return subject; }
        public String getPredicate() { return predicate; }
        public String getObject() { return object; }
    }

    // Endpoint: POST /information-extractor/neo4j-graph
    @PostMapping("/neo4j-graph")
    public List<TripleResponse> extractNeo4jTriples(@RequestBody InputRequest inputRequest) {
        List<Neo4jTriple> triples = knowledgeGraphNeo4jService.extractNeo4jTriples(inputRequest.getText());

        List<TripleResponse> response = new ArrayList<>();
        for (Neo4jTriple t : triples) {
            response.add(new TripleResponse(t.getSubject(), t.getPredicate(), t.getObject()));
        }
        return response;
    }
}
