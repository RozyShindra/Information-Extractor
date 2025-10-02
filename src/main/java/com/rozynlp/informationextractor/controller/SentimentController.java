package com.rozynlp.informationextractor.controller;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;

import edu.stanford.nlp.util.CoreMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/information-extractor")
public class SentimentController {

    private final StanfordCoreNLP stanfordCoreNLP;

    @Autowired
    public SentimentController(StanfordCoreNLP stanfordCoreNLP) {
        this.stanfordCoreNLP = stanfordCoreNLP;
    }

    // Input DTO for JSON input {"text": "..."}
    public static class InputRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // Output DTO for JSON output {"sentiment": "..."}
    public static class SentimentResponse {
        private String sentiment;

        public SentimentResponse(String sentiment) {
            this.sentiment = sentiment;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }
    }

    @PostMapping("/sentiment")
    public SentimentResponse predictSentiment(@RequestBody InputRequest inputRequest) {
        String text = inputRequest.getText();

        Annotation annotation = new Annotation(text);
        stanfordCoreNLP.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        // Default sentiment
        String sentiment = "neutral";

        if (sentences != null && !sentences.isEmpty()) {
            // Take sentiment from the first sentence for simplicity
            String coreSentiment = sentences.get(0).get(SentimentCoreAnnotations.SentimentClass.class);
            switch (coreSentiment.toLowerCase()) {
                case "very positive":
                case "positive":
                    sentiment = "positive";
                    break;
                case "very negative":
                case "negative":
                    sentiment = "negative";
                    break;
                default:
                    sentiment = "neutral";
                    break;
            }
        }

        return new SentimentResponse(sentiment);
    }
}
