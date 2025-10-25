package com.networkticket.ticket.model;

import com.networkticket.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    private Long customerId;
    
    private String location;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Enumerated(EnumType.STRING)
    private Severity severity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    private Instant slaStartAt;
    
    private Instant slaDueAt;
    
    @Column(nullable = false)
    private Boolean slaBreached = false;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (slaBreached == null) {
            slaBreached = false;
        }
    }
    
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
    
    public enum Severity {
        HIGH, MEDIUM, LOW
    }
    
    public enum Status {
        NEW, IN_PROGRESS, RESOLVED, CLOSED, ASSIGNED, ON_HOLD, REOPENED
    }
}