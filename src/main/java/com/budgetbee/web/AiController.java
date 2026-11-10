package com.budgetbee.web;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.security.CurrentUser;
import com.budgetbee.service.AiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/ai")
public class AiController {
    private final AiService ai; private final CurrentUser me;
    public AiController(AiService ai, CurrentUser me) { this.ai = ai; this.me = me; }

    @PostMapping("/chat") public ChatRes chat(@Valid @RequestBody ChatReq r) { return ai.chat(me.get(), r); }
    @GetMapping("/insight") public ChatRes insight() { return ai.insight(me.get()); }
}
