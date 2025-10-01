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
@RequestMapping("/information-extractor")
public class NERController {

    private final StanfordCoreNLP stanfordCoreNLP;

    @Autowired
    public NERController(StanfordCoreNLP stanfordCoreNLP) {
        this.stanfordCoreNLP = stanfordCoreNLP;
    }

    // Define a class to handle JSON input with a field "text"
    public static class InputRequest {
        private String text;

        // Getter and Setter
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }

    @PostMapping("/ner")
    public Map<String, String> ner(
            @RequestBody InputRequest inputRequest,
            @RequestParam(required = false) List<Type> types) {

        String input = inputRequest.getText();

        CoreDocument coreDocument = new CoreDocument(input);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();

        // If no types specified, use all types
        List<Type> effectiveTypes = (types == null || types.isEmpty()) ? Arrays.asList(Type.values()) : types;

        // Collect entities mapped to their type (originalText -> entityType)
        Map<String, String> result = coreLabels.stream()
                .filter(coreLabel -> effectiveTypes.stream()
                        .anyMatch(type -> type.getType().equalsIgnoreCase(
                                coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class))))
                .collect(Collectors.toMap(
                        CoreLabel::originalText,
                        cl -> cl.get(CoreAnnotations.NamedEntityTagAnnotation.class),
                        (existing, replacement) -> existing // if duplicate word, keep existing type
                ));

        return result;
    }
}
