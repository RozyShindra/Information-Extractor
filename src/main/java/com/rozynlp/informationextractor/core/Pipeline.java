package com.rozynlp.informationextractor.core;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class Pipeline {
    @Bean
    public StanfordCoreNLP stanfordCoreNLP() {
        Properties properties = new Properties();
        // No spaces between annotators as per Stanford CoreNLP convention
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        return new StanfordCoreNLP(properties);
    }
}
