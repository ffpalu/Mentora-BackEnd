package com.backend.mentora.entity.enums;

public enum PsychologistSpecialization {
    CHILD_PSYCHOLOGY("Psicologo dell'età evolutiva", 0,11),
    ADOLESCENT_PSYCHOLOGY("Psicologo per adolescenti", 12, 16),
    ADULT_PSYCHOLOGY("Psicologo per addulti", 17, 64),
    GERIATRIC_PSYCHOLOGY("Psicogerontologo", 65, 120);

    private final String description;
    private final int minAge;
    private final int maxAge;

    PsychologistSpecialization(String description, int minAge, int maxAge) {
        this.description = description;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public String getDescription() { return description; }
    public int getMinAge() { return minAge; }
    public int getMaxAge() { return maxAge; }

    public static PsychologistSpecialization getByAge(int age) {
        for (PsychologistSpecialization spec : values()) {
            if (age >= spec.getMinAge() && age <= spec.getMaxAge()) return spec;
            return spec;
        }
        return ADULT_PSYCHOLOGY;
    }
}
