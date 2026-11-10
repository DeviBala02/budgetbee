package com.budgetbee.repo;

import com.budgetbee.model.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface GoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserIdOrderByIdDesc(Long userId);
    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
}
