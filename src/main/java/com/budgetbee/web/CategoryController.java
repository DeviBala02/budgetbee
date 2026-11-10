package com.budgetbee.web;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService svc; private final CurrentUser me;
    public CategoryController(CategoryService svc, CurrentUser me) { this.svc = svc; this.me = me; }

    @GetMapping public List<CategoryRes> list() { return svc.list(me.get().getId()); }
    @PostMapping public CategoryRes create(@Valid @RequestBody CategoryReq r) { return svc.create(me.get(), r); }
}
