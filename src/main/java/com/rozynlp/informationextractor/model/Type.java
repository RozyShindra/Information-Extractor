package com.rozynlp.informationextractor.model;

public enum Type {
    PERSON("PERSON"),
    LOCATION("LOCATION"),
    ORGANIZATION("ORGANIZATION"),
    MISC("MISC"),                 // Miscellaneous
    DATE("DATE"),
    TIME("TIME"),
    DURATION("DURATION"),
    SET("SET"),                   // Collections like "group of people"
    MONEY("MONEY"),
    PERCENT("PERCENT"),
    EMAIL("EMAIL"),
    URL("URL"),
    PHONE("PHONE"),
    TITLE("TITLE"),
    CITY("CITY"),
    COUNTRY("COUNTRY"),
    STATE_OR_PROVINCE("STATE_OR_PROVINCE"),
    NATIONALITY("NATIONALITY"),
    RELIGION("RELIGION"),
    LANGUAGE("LANGUAGE"),
    EVENT("EVENT"),
    WORK_OF_ART("WORK_OF_ART"),
    LAW("LAW"),
    FACILITY("FACILITY");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
