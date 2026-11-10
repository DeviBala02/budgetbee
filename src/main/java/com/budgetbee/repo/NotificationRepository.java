package com.budgetbee.repo;

import com.budgetbee.model.Notification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndRefKey(Long userId, String refKey);
    long countByUserIdAndSeenFalse(Long userId);

    @Modifying
    @Query("update Notification n set n.seen=true where n.user.id=:uid and n.seen=false")
    int markAllSeen(@Param("uid") Long uid);
}
