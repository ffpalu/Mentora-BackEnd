package com.backend.mentora.entity.enums;

public enum Priority{
    NORMAL("Normale"),
    MODERATE("Gravità moderata - max 2 settimane"),
    HIGH("Priorità alta - max 1 settimana");

    private final String description;

    Priority(String description){
        this.description = description;
    }

    public String getDescription(){ return description; }
}
