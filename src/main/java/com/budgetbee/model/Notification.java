package com.budgetbee.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity 
@Table(name = "notifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ref_key"}))
@Getter 
@Setter 
@NoArgsConstructor
public class Notification {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id")
    private AppUser user;
    @Column(nullable = false) private String type;      // BUDGET_WARNING, BUDGET_EXCEEDED, GOAL_REACHED
    @Column(nullable = false, length = 500) private String message;
    /** De-duplication key, e.g. EXC:12:2026-11 -> one alert per category per month. */
    @Column(name = "ref_key", nullable = false) private String refKey;
    private boolean seen = false;
    private Instant createdAt = Instant.now();
}
