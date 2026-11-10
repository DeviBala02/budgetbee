package com.budgetbee.security;

import com.budgetbee.model.AppUser;
import com.budgetbee.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    private final UserRepository users;
    public CurrentUser(UserRepository users) { this.users = users; }

    public AppUser get() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return users.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
