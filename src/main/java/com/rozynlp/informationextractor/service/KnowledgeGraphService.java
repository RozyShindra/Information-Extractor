package com.rozynlp.informationextractor.service;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KnowledgeGraphService {

    private final StanfordCoreNLP pipeline;

    public KnowledgeGraphService(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    public Map<String, Object> extractKnowledge(String text) {
        Map<String, Object> result = new HashMap<>();

        CoreDocument doc = new CoreDocument(text);
        pipeline.annotate(doc);

        // 1. Extract Entities
        List<Map<String, String>> entities = new ArrayList<>();
        for (CoreLabel token : doc.tokens()) {
            String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            if (!"O".equals(ner)) { // skip non-entities
                Map<String, String> ent = new HashMap<>();
                ent.put("text", token.originalText());
                ent.put("type", ner);
                entities.add(ent);
            }
        }
        result.put("entities", entities);

        // 2. Coreference Resolution
        Map<Integer, CorefChain> corefChains = doc.corefChains();
        List<Map<String, Object>> corefs = new ArrayList<>();
        if (corefChains != null) {
            for (CorefChain chain : corefChains.values()) {
                Map<String, Object> corefEntry = new HashMap<>();
                corefEntry.put("representative", chain.getRepresentativeMention().mentionSpan);
                corefEntry.put("mentions", chain.getMentionsInTextualOrder().toString());
                corefs.add(corefEntry);
            }
        }
        result.put("coreferences", corefs);

        // 3. Relations: SVO Triple Extraction using dependency parse
        List<Map<String, String>> relations = new ArrayList<>();
        for (CoreSentence sentence : doc.sentences()) {
            SemanticGraph dependencies = sentence.dependencyParse();

            for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
                // Extract predicates (verbs), subjects, and objects
                GrammaticalRelation reln = edge.getRelation();
                String relation = reln.getShortName();

                if ("nsubj".equals(relation)) {
                    // Typical subject-verb
                    String subject = edge.getDependent().originalText();
                    String predicate = edge.getGovernor().originalText();

                    // Search for object for the same verb
                    for (SemanticGraphEdge objEdge : dependencies.edgeIterable()) {
                        if ("dobj".equals(objEdge.getRelation().getShortName())
                                && objEdge.getGovernor().equals(edge.getGovernor())) {
                            String object = objEdge.getDependent().originalText();
                            Map<String, String> triple = new HashMap<>();
                            triple.put("subject", subject);
                            triple.put("predicate", predicate);
                            triple.put("object", object);
                            relations.add(triple);
                        }
                    }
                }
            }
        }
        result.put("relations", relations);

        return result;
    }
}
