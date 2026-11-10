package com.budgetbee.web;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService svc; private final CurrentUser me;
    public ExpenseController(ExpenseService svc, CurrentUser me) { this.svc = svc; this.me = me; }

    @GetMapping public List<ExpenseRes> list() { return svc.list(me.get().getId()); }
    @PostMapping public ExpenseRes create(@Valid @RequestBody ExpenseReq r) { return svc.create(me.get(), r); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { svc.delete(me.get().getId(), id); }
}
