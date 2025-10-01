package com.rozynlp.informationextractor.model;

public enum Type {
    PERSON("PERSON"),
    LOCATION("LOCATION"),
    ORGANIZATION("ORGANIZATION"),
    EMAIL("EMAIL"),
    TITLE("TITLE"),
    CITY("CITY"),
    COUNTRY("COUNTRY"),
    STATE_OR_PROVINCE("STATE_OR_PROVINCE");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
