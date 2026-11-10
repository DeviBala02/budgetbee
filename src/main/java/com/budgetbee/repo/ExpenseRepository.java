package com.budgetbee.repo;

import com.budgetbee.model.Expense;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findTop100ByUserIdOrderBySpentOnDescIdDesc(Long userId);
    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(sum(e.amount),0) from Expense e " +
           "where e.user.id=:uid and e.category.id=:cid and e.spentOn between :from and :to")
    BigDecimal sumForCategory(@Param("uid") Long uid, @Param("cid") Long cid,
                              @Param("from") LocalDate from, @Param("to") LocalDate to);

    /** rows: [categoryId, sum] */
    @Query("select e.category.id, sum(e.amount) from Expense e " +
           "where e.user.id=:uid and e.spentOn between :from and :to group by e.category.id")
    List<Object[]> sumByCategory(@Param("uid") Long uid,
                                 @Param("from") LocalDate from, @Param("to") LocalDate to);
}
