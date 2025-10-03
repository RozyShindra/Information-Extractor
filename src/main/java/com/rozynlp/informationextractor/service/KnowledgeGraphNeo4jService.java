package com.rozynlp.informationextractor.service;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.coref.data.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.GrammaticalRelation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KnowledgeGraphNeo4jService {

    private final StanfordCoreNLP pipeline;

    public KnowledgeGraphNeo4jService(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    // Returns a list of Neo4jTriple objects
    public List<Neo4jTriple> extractNeo4jTriples(String text) {
        CoreDocument doc = new CoreDocument(text);
        pipeline.annotate(doc);

        Map<Integer, CorefChain> corefChains = doc.corefChains();
        Map<String, String> mentionToRepresentative = new HashMap<>();
        if (corefChains != null) {
            for (CorefChain chain : corefChains.values()) {
                String representative = chain.getRepresentativeMention().mentionSpan;
                for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
                    mentionToRepresentative.put(mention.mentionSpan, representative);
                }
            }
        }

        List<Neo4jTriple> triples = new ArrayList<>();

        for (CoreSentence sentence : doc.sentences()) {
            SemanticGraph dependencies = sentence.dependencyParse();

            // SVO and enriched relations
            for (IndexedWord verb : dependencies.vertexSet()) {
                String pos = verb.tag();
                if (pos != null && pos.startsWith("VB")) {
                    Set<IndexedWord> subjects = new HashSet<>(dependencies.getChildrenWithReln(verb, GrammaticalRelation.valueOf("nsubj")));
                    subjects.addAll(dependencies.getChildrenWithReln(verb, GrammaticalRelation.valueOf("nsubjpass")));

                    Set<IndexedWord> objects = new HashSet<>(dependencies.getChildrenWithReln(verb, GrammaticalRelation.valueOf("dobj")));
                    objects.addAll(dependencies.getChildrenWithReln(verb, GrammaticalRelation.valueOf("iobj")));
                    objects.addAll(dependencies.getChildrenWithReln(verb, GrammaticalRelation.valueOf("nmod")));

                    for (IndexedWord subj : subjects) {
                        String s = resolveCoref(mentionToRepresentative, subj.originalText());
                        for (IndexedWord obj : objects) {
                            String o = resolveCoref(mentionToRepresentative, obj.originalText());
                            triples.add(new Neo4jTriple(s, verb.originalText(), o));
                        }
                    }
                }
            }


            // Appositional relations
            for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
                if ("appos".equals(edge.getRelation().getShortName())) {
                    String s = edge.getGovernor().originalText();
                    String o = edge.getDependent().originalText();
                    triples.add(new Neo4jTriple(s, "is", o));
                }
            }
        }

        return triples;
    }

    // Helper to resolve coreferences
    private String resolveCoref(Map<String, String> mentionToRep, String mention) {
        return mentionToRep.getOrDefault(mention, mention);
    }

    // Neo4jTriple class for holding triples and output helpers
    public static class Neo4jTriple {
        private final String subject;
        private final String predicate;
        private final String object;

        public Neo4jTriple(String subject, String predicate, String object) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
        }

        // For Cypher statements
        public String toCypher() {
            return String.format(
                    "MERGE (a:Entity {name: '%s'}) MERGE (b:Entity {name: '%s'}) MERGE (a)-[:%s]->(b)",
                    sanitize(subject), sanitize(object), sanitize(predicate)
            );
        }

        // For CSV: subject,predicate,object
        public String toCSVRow() {
            return String.format("\"%s\",\"%s\",\"%s\"", sanitize(subject), sanitize(predicate), sanitize(object));
        }

        private String sanitize(String str) {
            return str.replace("\"", "'").replace("\\", "");
        }

        public String getSubject() { return subject; }
        public String getPredicate() { return predicate; }
        public String getObject() { return object; }
    }
}
