package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.*;
import com.budgetbee.repo.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository repo;
    private final CategoryService categories;
    public BudgetService(BudgetRepository repo, CategoryService categories) { this.repo = repo; this.categories = categories; }

    @Transactional(readOnly = true)
    public List<BudgetRes> list(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toRes).toList();
    }

    /** One budget per category: create or update the limit. */
    @Transactional
    public BudgetRes upsert(AppUser user, BudgetReq r) {
        Category cat = categories.owned(r.categoryId(), user.getId());
        Budget b = repo.findByUserIdAndCategoryId(user.getId(), cat.getId()).orElseGet(() -> {
            Budget nb = new Budget(); nb.setUser(user); nb.setCategory(cat); return nb;
        });
        b.setMonthlyLimit(r.monthlyLimit());
        return toRes(repo.save(b));
    }

    @Transactional
    public void delete(Long userId, Long id) {
        repo.delete(repo.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Budget not found")));
    }

    private BudgetRes toRes(Budget b) {
        return new BudgetRes(b.getId(), b.getCategory().getId(), b.getCategory().getName(), b.getMonthlyLimit());
    }
}
