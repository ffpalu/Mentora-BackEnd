package com.backend.mentora.entity.enums;

public enum AppointmentStatus {
	REQUESTED("Richiesto"),
	CONFIRMED("Confermato"),
	COMPLETED("Completato"),
	CANCELLED("Annullato"),
	REJECTED("Rifiutato");

	private final String description;

	AppointmentStatus(String description) {
		this.description = description;
	}

	public String getDescription() {return this.description;}
}
