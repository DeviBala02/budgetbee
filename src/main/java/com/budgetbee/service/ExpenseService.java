package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.*;
import com.budgetbee.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {
    private static final BigDecimal WARN_AT = new BigDecimal("0.80");

    private final ExpenseRepository expenses;
    private final BudgetRepository budgets;
    private final CategoryService categories;
    private final NotificationService notifications;

    public ExpenseService(ExpenseRepository expenses, BudgetRepository budgets,
                          CategoryService categories, NotificationService notifications) {
        this.expenses = expenses; this.budgets = budgets; this.categories = categories; this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<ExpenseRes> list(Long userId) {
        return expenses.findTop100ByUserIdOrderBySpentOnDescIdDesc(userId).stream().map(this::toRes).toList();
    }

    @Transactional
    public ExpenseRes create(AppUser user, ExpenseReq r) {
        Category cat = categories.owned(r.categoryId(), user.getId());
        Expense e = new Expense();
        e.setUser(user); e.setCategory(cat); e.setAmount(r.amount());
        e.setDescription(r.description());
        e.setSpentOn(r.spentOn() != null ? r.spentOn() : LocalDate.now());
        expenses.save(e);
        checkBudget(user, cat, e.getSpentOn());
        return toRes(e);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        expenses.delete(expenses.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found")));
    }

    /** Core feature: alert when a category crosses 80% or 100% of its monthly limit. */
    private void checkBudget(AppUser user, Category cat, LocalDate spentOn) {
        YearMonth ym = YearMonth.now();
        if (!YearMonth.from(spentOn).equals(ym)) return;          // only current-month spending triggers alerts
        budgets.findByUserIdAndCategoryId(user.getId(), cat.getId()).ifPresent(b -> {
            BigDecimal spent = expenses.sumForCategory(user.getId(), cat.getId(), ym.atDay(1), ym.atEndOfMonth());
            BigDecimal limit = b.getMonthlyLimit();
            if (spent.compareTo(limit) > 0) {
                BigDecimal over = spent.subtract(limit);
                notifications.push(user, "BUDGET_EXCEEDED",
                        "🚨 You've exceeded your " + cat.getName() + " budget by " + inr(over)
                                + " (spent " + inr(spent) + " of " + inr(limit) + ").",
                        "EXC:" + cat.getId() + ":" + ym);
            } else if (spent.compareTo(limit.multiply(WARN_AT)) >= 0) {
                notifications.push(user, "BUDGET_WARNING",
                        "⚠️ You've used 80%+ of your " + cat.getName() + " budget (" + inr(spent) + " of " + inr(limit) + ").",
                        "WARN:" + cat.getId() + ":" + ym);
            }
        });
    }

    static String inr(BigDecimal v) { return "₹" + v.setScale(0, RoundingMode.HALF_UP).toPlainString(); }

    private ExpenseRes toRes(Expense e) {
        return new ExpenseRes(e.getId(), e.getCategory().getId(), e.getCategory().getName(),
                e.getAmount(), e.getDescription(), e.getSpentOn());
    }
}
