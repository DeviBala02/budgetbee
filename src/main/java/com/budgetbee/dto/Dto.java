package com.budgetbee.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

public final class Dto {
    private Dto() {}

    public record RegisterReq(@NotBlank String name, @Email @NotBlank String email,
                              @Size(min = 6, message = "Password must be at least 6 characters") String password) {
                              }
    public record LoginReq(@NotBlank String email, @NotBlank String password) {}
    public record AuthRes(String token, String name) {}

    public record CategoryReq(@NotBlank String name) {}
    public record CategoryRes(Long id, String name) {}

    public record ExpenseReq(@NotNull Long categoryId, @NotNull @Positive BigDecimal amount,
                             String description, LocalDate spentOn) {}
    public record ExpenseRes(Long id, Long categoryId, String categoryName, BigDecimal amount,
                             String description, LocalDate spentOn) {}

    public record BudgetReq(@NotNull Long categoryId, @NotNull @Positive BigDecimal monthlyLimit) {}
    public record BudgetRes(Long id, Long categoryId, String categoryName, BigDecimal monthlyLimit) {}

    public record GoalReq(@NotBlank String name, @NotNull @Positive BigDecimal targetAmount, LocalDate deadline) {}
    public record ContributionReq(@NotNull @Positive BigDecimal amount) {}
    public record GoalRes(Long id, String name, BigDecimal targetAmount, BigDecimal savedAmount,
                          LocalDate deadline, int percent) {}

    public record NotificationRes(Long id, String type, String message, boolean seen, Instant createdAt) {}

    public record ChatMsg(String role, String content) {}
    public record ChatReq(@NotBlank String message, List<ChatMsg> history) {}
    public record ChatRes(String reply, boolean aiOnline) {}

    public record CategoryStatus(Long categoryId, String category, BigDecimal spent,
                                 BigDecimal limit, int percent) {}
    public record Dashboard(BigDecimal monthTotal, List<CategoryStatus> categories,
                            List<GoalRes> goals, long unreadNotifications) {}
}
