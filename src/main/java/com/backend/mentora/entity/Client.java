package com.backend.mentora.entity;

import com.backend.mentora.utils.SessionMode;
import com.backend.mentora.utils.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends User {

    @Column(nullable = false)
    private Integer age;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_session_mode")
    private SessionMode preferredSessionMode;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestionnaireResponse questionnaireResponse;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<ClientPsychologistRelation> psychologistRelation = new ArrayList<>();

    public Client(String email, String password, String firstName, String lastName, Integer age) {
        this.setEmail(email);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setRole(UserRole.CLIENT);
        this.setIsActive(true);
        this.age = age;
    }

    public boolean hasCompletedQuestionnaire(){
        return questionnaireResponse != null;
    }

    public List<ClientPsychologistRelation> getActiveRelations() {
        return psychologistRelation.stream()
                .filter(ClientPsychologistRelation::getIsActive)
                .toList();
    }

}
