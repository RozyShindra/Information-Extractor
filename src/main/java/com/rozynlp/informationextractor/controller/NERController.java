package com.rozynlp.informationextractor.controller;

import com.rozynlp.informationextractor.model.Type;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class NERController {

    private final StanfordCoreNLP stanfordCoreNLP;

    @Autowired
    public NERController(StanfordCoreNLP stanfordCoreNLP) {
        this.stanfordCoreNLP = stanfordCoreNLP;
    }

    @PostMapping("/ner")
    public Set<String> ner(@RequestBody String input, @RequestParam Type type) {
        CoreDocument coreDocument = new CoreDocument(input);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        return new HashSet<>(collectList(coreLabels, type));
    }

    private List<String> collectList(List<CoreLabel> coreLabels, Type type) {
        return coreLabels.stream()
                .filter(coreLabel -> type.getType().equalsIgnoreCase(
                        coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class)))
                .map(CoreLabel::originalText)
                .collect(Collectors.toList());
    }
}
