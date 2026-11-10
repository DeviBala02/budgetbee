package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.AppUser;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.*;

/**
 * "Bee" — the friendly finance coach. Talks to a local Ollama server (free, private).
 * If Ollama is not running, falls back to a rule-based answer so the app never breaks.
 */
@Service
public class AiService {
    private static final Logger log = LoggerFactory.getLogger(AiService.class);
    private static final String PERSONA = """
            You are Bee, a warm, upbeat personal finance coach inside the BudgetBee app.
            Use Indian Rupees (₹). Be concise (max 120 words), practical and encouraging, never judgmental.
            Base every statement ONLY on the user's data below; if data is missing, say so and suggest what to add.
            Give at most 3 concrete tips. You are not a licensed financial advisor.
            """;

    private final RestClient http;
    private final String model;
    private final DashboardService dashboard;

    public AiService(@Value("${app.ollama.url}") String url, @Value("${app.ollama.model}") String model,
                     DashboardService dashboard) {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(3_000);
        f.setReadTimeout(90_000);
        this.http = RestClient.builder().baseUrl(url).requestFactory(f).build();
        this.model = model;
        this.dashboard = dashboard;
    }

    public ChatRes chat(AppUser user, ChatReq req) {
        String context = dashboard.summaryText(user.getId());
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                PERSONA + "\nUser's name: " + user.getName() + "\nUSER DATA:\n" + context));
        if (req.history() != null) {
            req.history().stream().skip(Math.max(0, req.history().size() - 8)).forEach(m -> {
                String role = "assistant".equals(m.role()) ? "assistant" : "user";
                if (m.content() != null && !m.content().isBlank()) messages.add(Map.of("role", role, "content", m.content()));
            });
        }
        messages.add(Map.of("role", "user", "content", req.message()));
        try {
            JsonNode res = http.post().uri("/api/chat").contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("model", model, "stream", false, "messages", messages))
                    .retrieve().body(JsonNode.class);
            String text = res == null ? "" : res.path("message").path("content").asText("");
            if (!text.isBlank()) return new ChatRes(text.trim(), true);
        } catch (Exception e) {
            log.warn("Ollama unavailable ({}). Using fallback coach.", e.getMessage());
        }
        return new ChatRes(fallback(user, context), false);
    }

    /** One-tap spending insight for the dashboard. */
    public ChatRes insight(AppUser user) {
        return chat(user, new ChatReq("Give me a short insight about my spending this month and one tip to save more.", List.of()));
    }

    private String fallback(AppUser user, String context) {
        var d = dashboard.build(user.getId());
        List<String> over = d.categories().stream().filter(c -> c.limit() != null && c.percent() > 100)
                .map(CategoryStatus::category).toList();
        List<String> near = d.categories().stream().filter(c -> c.limit() != null && c.percent() >= 80 && c.percent() <= 100)
                .map(CategoryStatus::category).toList();
        StringBuilder sb = new StringBuilder("Hi " + user.getName() + "! 🐝 (Offline coach — start Ollama for smarter chat.) ");
        sb.append("You've spent ₹").append(d.monthTotal().setScale(0, java.math.RoundingMode.HALF_UP)).append(" this month. ");
        if (!over.isEmpty()) sb.append("Over budget in: ").append(String.join(", ", over)).append(". Try pausing spending there. ");
        if (!near.isEmpty()) sb.append("Close to the limit in: ").append(String.join(", ", near)).append(". ");
        if (over.isEmpty() && near.isEmpty()) sb.append("You're within all your budgets — nice work! ");
        if (d.goals().isEmpty()) sb.append("Tip: create a savings goal to stay motivated.");
        return sb.toString();
    }
}
