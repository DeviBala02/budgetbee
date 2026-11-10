package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.*;
import com.budgetbee.repo.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GoalService {
    private final GoalRepository repo;
    private final NotificationService notifications;

    public GoalService(GoalRepository repo, NotificationService notifications) {
        this.repo = repo; this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<GoalRes> list(Long userId) {
        return repo.findByUserIdOrderByIdDesc(userId).stream().map(GoalService::toRes).toList();
    }

    @Transactional
    public GoalRes create(AppUser user, GoalReq r) {
        SavingsGoal g = new SavingsGoal();
        g.setUser(user); g.setName(r.name().trim()); g.setTargetAmount(r.targetAmount()); g.setDeadline(r.deadline());
        return toRes(repo.save(g));
    }

    @Transactional
    public GoalRes contribute(AppUser user, Long id, ContributionReq r) {
        SavingsGoal g = repo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
        boolean wasDone = g.getSavedAmount().compareTo(g.getTargetAmount()) >= 0;
        g.setSavedAmount(g.getSavedAmount().add(r.amount()));
        if (!wasDone && g.getSavedAmount().compareTo(g.getTargetAmount()) >= 0) {
            notifications.push(user, "GOAL_REACHED",
                    "🎉 Goal reached: \"" + g.getName() + "\"! You saved " + ExpenseService.inr(g.getSavedAmount()) + ".",
                    "GOAL:" + g.getId());
        }
        return toRes(g);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        repo.delete(repo.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Goal not found")));
    }

    static GoalRes toRes(SavingsGoal g) {
        int pct = g.getTargetAmount().signum() == 0 ? 0
                : g.getSavedAmount().multiply(BigDecimal.valueOf(100))
                    .divide(g.getTargetAmount(), 0, RoundingMode.DOWN).min(BigDecimal.valueOf(100)).intValue();
        return new GoalRes(g.getId(), g.getName(), g.getTargetAmount(), g.getSavedAmount(), g.getDeadline(), pct);
    }
}
