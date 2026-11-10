package com.budgetbee.web;

import com.budgetbee.dto.Dto.Dashboard;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService svc; private final CurrentUser me;
    public DashboardController(DashboardService svc, CurrentUser me) { this.svc = svc; this.me = me; }

    @GetMapping public Dashboard get() { return svc.build(me.get().getId()); }
}
