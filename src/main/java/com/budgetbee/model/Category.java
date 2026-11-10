package com.budgetbee.model;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
@Getter 
@Setter 
@NoArgsConstructor
public class Category {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) 
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id")
    private AppUser user;
}
