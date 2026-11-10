package com.budgetbee.web;

import com.budgetbee.dto.Dto.NotificationRes;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService svc; private final CurrentUser me;
    public NotificationController(NotificationService svc, CurrentUser me) { this.svc = svc; this.me = me; }

    @GetMapping public List<NotificationRes> list() { return svc.list(me.get().getId()); }
    @PostMapping("/read-all") public void readAll() { svc.markAllSeen(me.get().getId()); }
}
