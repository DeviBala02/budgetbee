package com.budgetbee.repo;

import com.budgetbee.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    Optional<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
}
