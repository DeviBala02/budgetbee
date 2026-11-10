package com.budgetbee.service;

import com.budgetbee.dto.Dto.NotificationRes;
import com.budgetbee.model.*;
import com.budgetbee.repo.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    public NotificationService(NotificationRepository repo) { this.repo = repo; }

    /** Creates a notification once per refKey (so the same alert isn't repeated). */
    @Transactional
    public void push(AppUser user, String type, String message, String refKey) {
        if (repo.existsByUserIdAndRefKey(user.getId(), refKey)) return;
        Notification n = new Notification();
        n.setUser(user); n.setType(type); n.setMessage(message); n.setRefKey(refKey);
        repo.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationRes> list(Long userId) {
        return repo.findTop50ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> new NotificationRes(n.getId(), n.getType(), n.getMessage(), n.isSeen(), n.getCreatedAt()))
                .toList();
    }

    @Transactional public void markAllSeen(Long userId) { repo.markAllSeen(userId); }
    public long unread(Long userId) { return repo.countByUserIdAndSeenFalse(userId); }
}
