package com.budgetbee.repo;

import com.budgetbee.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrderByName(Long userId);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
