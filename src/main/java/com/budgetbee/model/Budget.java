package com.budgetbee.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/** Monthly spending limit for one category; repeats every month */
@Entity 
@Table(name = "budgets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id"}))
@Getter 
@Setter 
@NoArgsConstructor
public class Budget {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) 
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(nullable = false, precision = 12, scale = 2) 
    private BigDecimal monthlyLimit;
}
