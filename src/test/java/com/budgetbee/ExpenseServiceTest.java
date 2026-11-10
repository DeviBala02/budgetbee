package com.budgetbee;

import com.budgetbee.dto.Dto.ExpenseReq;
import com.budgetbee.model.*;
import com.budgetbee.repo.*;
import com.budgetbee.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    private final ExpenseRepository expenses = mock(ExpenseRepository.class);
    private final BudgetRepository budgets = mock(BudgetRepository.class);
    private final CategoryService categories = mock(CategoryService.class);
    private final NotificationService notifications = mock(NotificationService.class);
    private final ExpenseService service = new ExpenseService(expenses, budgets, categories, notifications);

    private AppUser user() { AppUser u = new AppUser(); u.setId(1L); return u; }
    private Category cat() { Category c = new Category(); c.setId(10L); c.setName("Food"); return c; }

    private void givenBudget(String limit, String spent) {
        Budget b = new Budget(); b.setMonthlyLimit(new BigDecimal(limit));
        when(categories.owned(anyLong(), anyLong())).thenReturn(cat());
        when(budgets.findByUserIdAndCategoryId(1L, 10L)).thenReturn(Optional.of(b));
        when(expenses.sumForCategory(eq(1L), eq(10L), any(), any())).thenReturn(new BigDecimal(spent));
    }

    private void add() {
        service.create(user(), new ExpenseReq(10L, new BigDecimal("100"), "test", LocalDate.now()));
    }

    @Test
    void exceedingLimitCreatesExceededAlert() {
        givenBudget("5000", "5200");
        add();
        ArgumentCaptor<String> type = ArgumentCaptor.forClass(String.class);
        verify(notifications).push(any(), type.capture(), anyString(), anyString());
        org.junit.jupiter.api.Assertions.assertEquals("BUDGET_EXCEEDED", type.getValue());
    }

    @Test
    void reaching80PercentCreatesWarning() {
        givenBudget("5000", "4200");
        add();
        ArgumentCaptor<String> type = ArgumentCaptor.forClass(String.class);
        verify(notifications).push(any(), type.capture(), anyString(), anyString());
        org.junit.jupiter.api.Assertions.assertEquals("BUDGET_WARNING", type.getValue());
    }

    @Test
    void belowThresholdCreatesNoAlert() {
        givenBudget("5000", "1000");
        add();
        verify(notifications, never()).push(any(), anyString(), anyString(), anyString());
    }
}
