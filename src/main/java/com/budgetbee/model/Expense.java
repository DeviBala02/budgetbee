package com.budgetbee.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity 
@Table(name = "expenses")
@Getter 
@Setter 
@NoArgsConstructor
public class Expense {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "category_id")
    private Category category;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    private String description;
    @Column(nullable = false) private LocalDate spentOn;
}
