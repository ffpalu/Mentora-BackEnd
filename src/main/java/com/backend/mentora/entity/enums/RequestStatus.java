package com.backend.mentora.entity.enums;

public enum RequestStatus {
    PENDING("In attesa"),
    ACCEPTED("Accettata"),
    REJECTED("Rifiutata");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

}
