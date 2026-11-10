package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.*;
import com.budgetbee.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {
    private final ExpenseRepository expenses;
    private final BudgetRepository budgets;
    private final CategoryRepository categories;
    private final GoalService goals;
    private final NotificationService notifications;

    public DashboardService(ExpenseRepository expenses, BudgetRepository budgets, CategoryRepository categories,
                            GoalService goals, NotificationService notifications) {
        this.expenses = expenses; this.budgets = budgets; this.categories = categories;
        this.goals = goals; this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public Dashboard build(Long userId) {
        YearMonth ym = YearMonth.now();
        Map<Long, BigDecimal> spentBy = new HashMap<>();
        for (Object[] row : expenses.sumByCategory(userId, ym.atDay(1), ym.atEndOfMonth()))
            spentBy.put((Long) row[0], (BigDecimal) row[1]);
        Map<Long, BigDecimal> limitBy = new HashMap<>();
        for (Budget b : budgets.findByUserId(userId)) limitBy.put(b.getCategory().getId(), b.getMonthlyLimit());

        List<CategoryStatus> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Category c : categories.findByUserIdOrderByName(userId)) {
            BigDecimal spent = spentBy.getOrDefault(c.getId(), BigDecimal.ZERO);
            BigDecimal limit = limitBy.get(c.getId());
            if (spent.signum() == 0 && limit == null) continue;
            int pct = (limit == null || limit.signum() == 0) ? 0
                    : spent.multiply(BigDecimal.valueOf(100)).divide(limit, 0, RoundingMode.HALF_UP).intValue();
            rows.add(new CategoryStatus(c.getId(), c.getName(), spent, limit, pct));
            total = total.add(spent);
        }
        return new Dashboard(total, rows, goals.list(userId), notifications.unread(userId));
    }

    /** Plain-text snapshot fed to the AI coach as context. */
    public String summaryText(Long userId) {
        Dashboard d = build(userId);
        StringBuilder sb = new StringBuilder("Month so far: total spent ₹" + d.monthTotal().setScale(0, RoundingMode.HALF_UP) + ".\n");
        sb.append("Day ").append(java.time.LocalDate.now().getDayOfMonth()).append(" of ")
          .append(YearMonth.now().lengthOfMonth()).append(".\nCategories:\n");
        for (CategoryStatus c : d.categories())
            sb.append("- ").append(c.category()).append(": spent ₹").append(c.spent().setScale(0, RoundingMode.HALF_UP))
              .append(c.limit() == null ? " (no limit set)" : " of ₹" + c.limit().setScale(0, RoundingMode.HALF_UP) + " (" + c.percent() + "%)")
              .append("\n");
        sb.append("Savings goals:\n");
        if (d.goals().isEmpty()) sb.append("- none yet\n");
        for (GoalRes g : d.goals())
            sb.append("- ").append(g.name()).append(": ₹").append(g.savedAmount().setScale(0, RoundingMode.HALF_UP))
              .append(" of ₹").append(g.targetAmount().setScale(0, RoundingMode.HALF_UP)).append(" (").append(g.percent()).append("%)\n");
        return sb.toString();
    }
}
