package com.budgetbee.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity 
@Table(name = "users")
@Getter 
@Setter 
@NoArgsConstructor
public class AppUser {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String passwordHash;
    private Instant createdAt = Instant.now();
}
