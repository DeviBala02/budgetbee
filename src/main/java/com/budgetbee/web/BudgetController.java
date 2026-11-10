package com.budgetbee.web;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService svc; private final CurrentUser me;
    public BudgetController(BudgetService svc, CurrentUser me) { this.svc = svc; this.me = me; }

    @GetMapping public List<BudgetRes> list() { return svc.list(me.get().getId()); }
    @PostMapping public BudgetRes upsert(@Valid @RequestBody BudgetReq r) { return svc.upsert(me.get(), r); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { svc.delete(me.get().getId(), id); }
}
