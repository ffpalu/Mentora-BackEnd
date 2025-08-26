package com.backend.mentora.entity.enums;

public enum SessionMode{
    IN_PERSON("In presenza"),
    ONLINE("Online"),
    MIXED("Moddalità mista"),
    INDIFFERENT("Indifferente");

    private final String description;

    SessionMode(String description){
        this.description = description;
    }

    public String getDescription(){ return description; }
}
