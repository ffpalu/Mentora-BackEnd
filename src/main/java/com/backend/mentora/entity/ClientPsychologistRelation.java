package com.backend.mentora.entity;

import com.backend.mentora.entity.enums.Priority;
import com.backend.mentora.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "client_psychologist_relations", uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "psychologist_id", "is_active"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPsychologistRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psychologist_id", nullable = false)
    private Psychologist psychologist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "client_message", columnDefinition = "TEXT")
    private String clientMessage;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }

    public void accept(String notes){
        this.status = RequestStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
        this.notes = notes;
    }

    public void reject(String reason) {
        this.status = RequestStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
        this.notes = reason;
        this.isActive = false;
    }

    public boolean isPending() {
        return RequestStatus.PENDING.equals(this.status);
    }

    public boolean isAccepted() {
        return RequestStatus.ACCEPTED.equals(this.status);
    }

    public boolean canCommunicate() {
        return isAccepted() && isActive;
    }

    public String getClientFullName(){
        return client != null ? client.getFullName() : "";
    }

    public String getPsychologistFullName(){
        return psychologist != null ? psychologist.getFullName() : "";
    }

    public Priority getClientPriority(){
        if(client != null && client.getQuestionnaireResponse() != null) {
            return client.getQuestionnaireResponse().getCalculatedPriority();
        }
        return Priority.NORMAL;
    }

}
