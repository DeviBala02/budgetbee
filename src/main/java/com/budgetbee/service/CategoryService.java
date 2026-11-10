package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.*;
import com.budgetbee.repo.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    public CategoryService(CategoryRepository repo) { this.repo = repo; }

    public List<CategoryRes> list(Long userId) {
        return repo.findByUserIdOrderByName(userId).stream().map(c -> new CategoryRes(c.getId(), c.getName())).toList();
    }

    public CategoryRes create(AppUser u, CategoryReq r) {
        Category c = new Category(); c.setName(r.name().trim()); c.setUser(u);
        try { repo.save(c); } catch (Exception e) { throw new IllegalArgumentException("Category already exists"); }
        return new CategoryRes(c.getId(), c.getName());
    }

    public Category owned(Long id, Long userId) {
        return repo.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Unknown category"));
    }
}
