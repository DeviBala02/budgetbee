package com.budgetbee.web;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/goals")
public class GoalController {
    private final GoalService svc; private final CurrentUser me;
    public GoalController(GoalService svc, CurrentUser me) { this.svc = svc; this.me = me; }

    @GetMapping public List<GoalRes> list() { return svc.list(me.get().getId()); }
    @PostMapping public GoalRes create(@Valid @RequestBody GoalReq r) { return svc.create(me.get(), r); }
    @PostMapping("/{id}/contribute")
    public GoalRes contribute(@PathVariable Long id, @Valid @RequestBody ContributionReq r) { return svc.contribute(me.get(), id, r); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { svc.delete(me.get().getId(), id); }
}
